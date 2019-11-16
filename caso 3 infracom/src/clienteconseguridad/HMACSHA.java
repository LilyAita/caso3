package clienteconseguridad;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class HMACSHA 
{
	static public String calcHmacSha256(SecretKey secretKey, String message) throws Exception
	{
	    byte[] hmacSha256 = null;
	    try {
	      Mac mac = Mac.getInstance("HmacSHA256");
	      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "HmacSHA256");
	      mac.init(secretKeySpec);
	      hmacSha256 = mac.doFinal(DatatypeConverter.parseBase64Binary(message));
	    } catch (Exception e) {
	      throw new Exception("Error calculando hmac: "+ e.getMessage());
	    }
	    return DatatypeConverter.printBase64Binary(hmacSha256);
	  }
}
