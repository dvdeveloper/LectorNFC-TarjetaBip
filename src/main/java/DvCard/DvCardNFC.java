package DvCard;

import java.nio.ByteBuffer;
import javax.smartcardio.*;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author DvDeveloper
 * @website dvdeveloper.com | diegovalladares.cl
 */
public class DvCardNFC {
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    private CardTerminal terminal = null;
    private TerminalFactory factory = TerminalFactory.getDefault();
   
    private String nombreDispositivo = null;
    private List<CardTerminal> terminals;
    
    private byte TIPO_A = (byte) 0x60;
    private byte TIPO_B = (byte) 0x61;
    
    public String dispositivo(){
        try{
            terminals = factory.terminals().list();
            return nombreDispositivo = terminals.get(0).toString();
            
        }catch(Exception ex){
            return "";
        }
    }
    
    public void getTarjeta() {
        
        try {
            terminals = factory.terminals().list();
            terminal = terminals.get(0);
            terminal.waitForCardPresent(0);

            Card card = terminal.connect("*");
            CardChannel canal = card.getBasicChannel();

            System.out.println("Llave UID: " +  EnviarAPDU(cargarLlave(BIP.getInstance().key_33), canal));
            System.out.println("Login UID: " +  EnviarAPDU(authentication(TIPO_B), canal));
            System.out.println("Leer UID: " +   EnviarAPDU(leerBlock(34), canal));


            BIP.getInstance().setSaldo(toByteArray(EnviarAPDU(leerBlock(34), canal)));
            BIP.getInstance().setTarjetaATR( card.getATR().getBytes() );
            
            card.disconnect(true);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Dispositivo y/o tarjeta desconectada");
            ex.printStackTrace();
        }
    }
    
    public String EnviarAPDU(byte[] cmd, CardChannel _canal) {
        
        String res = "";

        byte[] respuesta = new byte[258];
        ByteBuffer buffer_cmd = ByteBuffer.wrap(cmd);
        ByteBuffer buffer_resp = ByteBuffer.wrap(respuesta);

        int output = 0;

        try {
            output = _canal.transmit(buffer_cmd, buffer_resp);
        } catch (CardException ex) {
            ex.printStackTrace();
        }
        
        for (int i = 0; i < output; i++) {
            res += String.format("%02X", respuesta[i]);
        }
        return res;
    }
    
    public byte[] cargarLlave(byte[] llave){
        byte[] contenido = new byte[10];
        contenido = new byte[] { (byte) 0xFF, (byte) 0x82, (byte) 0x00,(byte) 0x00, (byte) 0x06 };
        
        ByteBuffer unir = ByteBuffer.allocate(contenido.length + llave.length);
        unir.put(contenido);
        unir.put(llave);
        return unir.array();
    }
    
    public byte[] authentication(byte TIPO){
        return new byte[] { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x01, (byte) 0x00, (byte) 0x21, TIPO , (byte) 0x00  };
    }
    
    public byte[] leerBlock(int block){
        byte[] rblock = new byte[4];
        rblock = new byte[] { (byte) 0xFF, (byte) 0xB0, (byte) 0x00, (byte) (block & 0xFF), (byte) 0x10 };
        return rblock;
    }
    
    private byte[] toByteArray(final String hexString) {

        final int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        for (i = 0; i < hexStringLength; i++) {
            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[len] = (byte) (value << 4);

                } else {
                    byteArray[len] |= value;
                    len++;
                }

                first = !first;
            }
        }

        return byteArray;
    }
    
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
}