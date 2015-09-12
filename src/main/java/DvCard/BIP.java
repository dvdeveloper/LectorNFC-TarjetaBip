package DvCard;

/**
 *
 * @author DvDeveloper
 * @website dvdeveloper.com | diegovalladares.cl
 */
public class BIP {
    
    public byte[] key_33 = new byte[]{ (byte) 0x64, (byte) 0xE3, (byte) 0xC1, (byte) 0x03, (byte) 0x94, (byte) 0xC2 };
    
    private static BIP singleton = null;
    private byte[] saldo;
    private byte[] tarjetaATR;

    protected BIP(){}
    
    
    public String getSaldo() {
        return formatMoneda(leToNumeric(saldo, 2));
    }

    public void setSaldo(byte[] saldo) {
        this.saldo = saldo;
    }

    public byte[] getTarjetaATR() {
        return tarjetaATR;
    }

    public void setTarjetaATR(byte[] tarjetaATR) {
        this.tarjetaATR = tarjetaATR;
    }
    
    
    
    public static BIP getInstance() {
      if(singleton == null) {
         singleton = new BIP();
      }
      return singleton;
    }
    
    private long leToNumeric(byte[] buffer, int size) {
    	long value = 0;
    	for (int i=0; i<size; i++) { value += ((long) buffer[i] & 0xffL) << (8 * i); }
    	return value;
    }
    
    private String leToNumericString(byte[] buffer, int size) {
    	return String.valueOf(leToNumeric(buffer, size));
    }
    
    private String formatMoneda(long valor) {
    	return "$"+String.format("%,d", valor);
    }
}
