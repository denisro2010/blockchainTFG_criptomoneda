package blockchain;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.GsonBuilder;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

public class StringUtils {
		
		//Aplica SHA256 a un string y devuelve el resultado
		public static String applySha256(String pCadena){
			
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
		        
				//Aplica SHA256 a la cadena de entrada 
				byte[] hash = digest.digest(pCadena.getBytes("UTF-8"));
		        
				StringBuffer hexString = new StringBuffer(); // hash en hexadecimal
				for (int i = 0; i < hash.length; i++) {
					String hex = Integer.toHexString(0xff & hash[i]);
					if(hex.length() == 1) hexString.append('0');
					hexString.append(hex);
				}
				return hexString.toString();
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		//Aplica ECDSA y devuelve el resultado como bytes
		public static byte[] applyECDSASig(PrivateKey pClavePrivada, String pCadena) {
			Signature dsa;
			byte[] resultado = new byte[0];
			try {
				dsa = Signature.getInstance("ECDSA", "BC");
				dsa.initSign(pClavePrivada);
				byte[] strByte = pCadena.getBytes();
				dsa.update(strByte);
				byte[] realSig = dsa.sign();
				resultado = realSig;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return resultado;
		}
		
		//Verifica una firma
		public static boolean verifyECDSASig(PublicKey pCalvePublica, String pDatos, byte[] pFirma) {
			try {
				Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
				ecdsaVerify.initVerify(pCalvePublica);
				ecdsaVerify.update(pDatos.getBytes());
				return ecdsaVerify.verify(pFirma);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		//Convierte un Object en GSON
		public static String getJson(Object o) {
			return new GsonBuilder().setPrettyPrinting().create().toJson(o);
		}
		
		//Devuelve el numero de ceros correspondiente a la dificultad para compararlo con el hash -> d=5 ---> devuelve '00000'
		public static String getStringDificultad(int pDificultad) {
			return new String(new char[pDificultad]).replace('\0', '0');
		}
		
		public static String getStringClave(Key pClave) {
			return Base64.getEncoder().encodeToString(pClave.getEncoded());
		}
		
		public static Key getClaveDesdeString(String pClave, boolean pPublica) {
			byte[] decodedKey = Base64.getDecoder().decode(pClave);
			
			Key key = null;
			
			if(pPublica == true) {
				try {
					key = KeyFactory.getInstance("ECDSA").generatePublic(new X509EncodedKeySpec(decodedKey));
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					key = KeyFactory.getInstance("ECDSA").generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			return key; 
		}
		
		//Devuelve la raiz del arbol merkle
		public static String getMerkleRoot(ArrayList<Transaccion> pTransacciones) {
			int cont = pTransacciones.size();
			
			List<String> ramaAnteriorArbol = new ArrayList<String>();
			for(Transaccion transaccion : pTransacciones) {
				ramaAnteriorArbol.add(transaccion.IDtransaccion);
			}
			List<String> ramaArbol = ramaAnteriorArbol;
			
			while(cont > 1) {
				ramaArbol = new ArrayList<String>();
				for(int i=1; i < ramaAnteriorArbol.size(); i+=2) {
					ramaArbol.add(applySha256(ramaAnteriorArbol.get(i-1) + ramaAnteriorArbol.get(i)));
				}
				cont = ramaArbol.size();
				ramaAnteriorArbol = ramaArbol;
			}
			
			String raizMerkle = (ramaArbol.size() == 1) ? ramaArbol.get(0) : "";
			return raizMerkle;
		}
	
}
