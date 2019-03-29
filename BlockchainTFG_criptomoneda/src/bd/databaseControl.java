package bd;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import algoritmosCriptograficos.Aes;
import algoritmosCriptograficos.StringUtils;
import blockchain.Bloque;
import blockchain.Cartera;
import blockchain.ProgramaPrincipal;
import blockchain.SalidaTransaccion;
import blockchain.SmartContract;
import blockchain.Transaccion;
 
public class databaseControl {
    

	public static Connection connect() {
	   
	        Connection conn = null;
	        try {     
	            String url = "jdbc:sqlite:blockchainTFG.db";
	            conn = DriverManager.getConnection(url);
	            
	           // System.out.println("La conexión a la base de datos se ha realizado con éxito.");
	            
	        } catch (SQLException e) {
	            //System.out.println(e.getMessage());
	        }
	        /*finally {
	            try {
	                if (conn != null) {
	                    conn.close();
	                }
	            } catch (SQLException ex) {
	                System.out.println(ex.getMessage());
	            }
	        }*/
	        
			return conn;
	}
	
	 public static void tablaBloque() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS bloque (hash TEXT PRIMARY KEY NOT NULL, hashAnterior TEXT NOT NULL UNIQUE, marcaTemporal BIGINT NOT NULL, nonce INTEGER NOT NULL, merkleRoot STRING, transaccion STRING REFERENCES transaccion (IDtran) NOT NULL);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	            //System.out.println(e.getMessage());
	         }
	    }
	 
	 public static void tablaCartera() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS cartera (usuario STRING NOT NULL PRIMARY KEY, contrasena TEXT NOT NULL, clavePublica TEXT NOT NULL UNIQUE, clavePrivada TEXT NOT NULL);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	            //System.out.println(e.getMessage());
	         }
	    }

	 public static void tablaOutputs() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS outputs (IDoutput TEXT NOT NULL PRIMARY KEY, cantidad DOUBLE NOT NULL, IDtransaccion TEXT NOT NULL, IDcartera TEXT REFERENCES cartera (clavePublica) NOT NULL);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	            //System.out.println(e.getMessage());
	         }
	 }
	 
	 public static void tablaTransaccion() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS transaccion (IDtran TEXT PRIMARY KEY, remitente TEXT NOT NULL, receptor TEXT NOT NULL, valor DOUBLE NOT NULL, firma TEXT NOT NULL, secuencia INTEGER);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	           // System.out.println(e.getMessage());
	         }
	    }
	 
	 public static void tablaSmartContracts() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS smartContract (IDsc TEXT PRIMARY KEY, Fecha BIGINT NOT NULL, Cantidad INT NOT NULL, Remitente TEXT REFERENCES cartera (clavePublica), Receptor TEXT REFERENCES cartera (clavePublica), FirmaTransaccion TEXT UNIQUE NOT NULL);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	           // System.out.println(e.getMessage());
	         }
	    }
	 
	 public static void crearCartera(String pUsuario, String pContra, PublicKey pClavePublica, PrivateKey pClavePrivada) throws Exception {
	        String sql = "INSERT INTO cartera(usuario, contrasena, clavePublica, clavePrivada) VALUES(?,?,?,?)";
	 
	        String contraHash = StringUtils.applySha3_256(pContra);
	        String publica = StringUtils.getStringClave(pClavePublica);
	        String privada = Aes.encrypt(StringUtils.getStringClave(pClavePrivada), pContra);
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pUsuario);
		            pstmt.setString(2, contraHash);
		            pstmt.setString(3, publica);
		            pstmt.setString(4, privada);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	        	//System.out.println(e.getMessage());
	        }
	    }
	 
	 public static void crearTransaccion(String pIDtran, String pRemitente, String pReceptor, float pValor, byte[] pFirma, int pSecuencia) throws Exception {
	        String sql = "INSERT INTO transaccion(IDtran, remitente, receptor, valor, firma, secuencia) VALUES(?,?,?,?,?,?)";
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pIDtran);
		            pstmt.setString(2, pRemitente);
		            pstmt.setString(3, pReceptor);
		            pstmt.setFloat(4, pValor);
		            pstmt.setBytes(5, pFirma);
		            pstmt.setInt(6, pSecuencia);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	            //System.out.println(e.getMessage());
	        }
	    }
	 
	 /*public static void crearBloque(String pHash, String pHashAnterior, long pMarcaTemp, int pNonce, String pMerkleRoot, String pTransaccion, String pContract) throws Exception {
	        String sql = "INSERT INTO bloque(hash, hashAnterior, marcaTemporal, nonce, merkleRoot, transaccion) VALUES(?,?,?,?,?,?,?)";
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pHash);
		            pstmt.setString(2, pHashAnterior);
		            pstmt.setLong(3, pMarcaTemp);
		            pstmt.setInt(4, pNonce);
		            pstmt.setString(5, pMerkleRoot);
		            pstmt.setString(6, pTransaccion);
		            pstmt.setString(7, pContract);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	        	// System.out.println(e.getMessage());
	        }
	    }*/
	 
	 public static void crearOutput(String pID, float pCant, String pIDtran, String pIDcartera) throws Exception {
	        String sql = "INSERT INTO outputs(IDoutput, cantidad, IDtransaccion, IDcartera) VALUES(?,?,?,?)";
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pID);
		            pstmt.setDouble(2, pCant);
		            pstmt.setString(3, pIDtran);
		            pstmt.setString(4, pIDcartera);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	        	// System.out.println(e.getMessage());
	        }
	    }
	 
	 public static String getPass(String pUsuario) throws Exception {
	   	 String pass = null;
	   	 String sql = "SELECT contrasena FROM cartera WHERE usuario='" + pUsuario + "';";
	   	 
		        try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 pass = rs.getString("contrasena");	
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	// System.out.println(se.getMessage());
		        }
		 return pass;
	   }
	 
	 public static String getUsuario(String pUsuario) throws Exception {
	   	 String usr = null;
		 String sql = "SELECT usuario FROM cartera WHERE usuario='" + pUsuario + "';";
	   	 
		        try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 usr = rs.getString("usuario");	
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		            //System.out.println(se.getMessage());
		        }
		 return usr;
	   }
	 
	 public static Cartera getCartera(String pUsuario, String pContra) throws Exception {
		 Cartera cartera = new Cartera();
		 String sql = "SELECT clavePublica, clavePrivada FROM cartera WHERE usuario='" + pUsuario + "';";
	   	 
		        try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 cartera.setClavePublica((PublicKey) StringUtils.getClaveDesdeString(rs.getString("clavePublica"), true));	
		        		 cartera.setClavePrivada((PrivateKey) StringUtils.getClaveDesdeString(Aes.decrypt(rs.getString("clavePrivada"), pContra), false));	
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		            //System.out.println(se.getMessage());
		        }
		 return cartera;
	   }
	 
	 public static void borrarCartera(String pIDcartera) throws Exception {
	    	String sql = "DELETE FROM cartera WHERE clavePublica='"+pIDcartera+"';";
	    	
	    	borrarOutputs(pIDcartera); 
	    	
	        try (Connection conn =  connect();
	                PreparedStatement pstmt = conn.prepareStatement(sql)) {
	 
	            // execute the delete statement
	            pstmt.executeUpdate();
	            pstmt.close();
	            conn.close();
	            
	        } catch (SQLException e) {
	        	// System.out.println(e.getMessage());
	        }
	      }
	 
	 public static void borrarOutput(String pIDoutput) throws Exception {
		 String sql = "DELETE FROM outputs WHERE IDoutput='"+pIDoutput+"';";
    	 
	        try (Connection conn =  connect();
	                PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
	            pstmt.executeUpdate();
	            pstmt.close();
	            conn.close();
	            
	        } catch (SQLException e) {
	        	//  System.out.println(e.getMessage());
	        }
	 }
	 
	 public static void borrarOutputs(String pIDcartera) throws Exception {
		 String sql = "DELETE FROM outputs WHERE IDcartera='"+pIDcartera+"';";
    	 
	        try (Connection conn =  connect();
	                PreparedStatement pstmt = conn.prepareStatement(sql)) {
	 
	            // execute the delete statement
	            pstmt.executeUpdate();
	            pstmt.close();
	            conn.close();
	            
	        } catch (SQLException e) {
	        	//  System.out.println(e.getMessage());
	        }
	 }
	 
	 public static boolean checkTablaBloquesVacia() {
		 int numFilas = -1;
		 
		 String sql = "SELECT count(*) AS total FROM bloque;";
	   	 
	        try (Connection conn =  connect();
	             PreparedStatement stmt  = conn.prepareStatement(sql);
	             ResultSet rs    = stmt.executeQuery()){
	        	 while (rs.next()) {
	        		numFilas = rs.getInt("total");
	        	 }
	        	 rs.close();
	        	 stmt.close();
	             conn.close();
	             
	        } catch (SQLException se) {
	        	//  System.out.println(se.getMessage());
	        }
	    if(numFilas == 0)
	    	return true;
	    else
	    	return false;
	 }
	 
	 public static void getOutputsMain() throws Exception {
		 	String id;
		 	String sql = "SELECT * FROM outputs;";
	   	 
		        try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 id = rs.getString("IDoutput");
		        		 SalidaTransaccion salida = new SalidaTransaccion((PublicKey) StringUtils.getClaveDesdeString(rs.getString("IDcartera"), true), rs.getFloat("cantidad"), rs.getString("IDtransaccion"));	
		        		 ProgramaPrincipal.getTransaccionesNoGastadas().put(id, salida);
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//  System.out.println(se.getMessage());
		        }
	   }
	 
	 public static int getSecuenciaMayor() throws Exception {
		 	String sql = "SELECT MAX(secuencia) AS sec FROM transaccion;";
		 	int secuencia = 0;
	   	 
		        try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		secuencia = rs.getInt("sec");
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//   System.out.println(se.getMessage());
		        }
		    return secuencia;
	   }
	 
	 public static String getHashUltimoBloque() throws Exception {
		 	String hash = null;
		 	String sql = "SELECT MAX(rowid), hash FROM bloque;";
	   	 
		        try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 hash = rs.getString("hash");
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//  System.out.println(se.getMessage());
		        }
		        
		    if(hash == null)
		        return "0"; //Significa que no hay bloques y que se va a crear el bloque génesis
		    else
		    	return hash;
	   }
    
	 public static void insertarBloque(Bloque pBloque) throws Exception {
	        String sql = "INSERT INTO bloque(hash, hashAnterior, marcaTemporal, nonce, transaccion, contrato, contratoConfirmado, contratoEjecutado) VALUES(?,?,?,?,?,?,?,?)";
	        
	        try (Connection conn =  connect();
	                PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, pBloque.getHash());
	            pstmt.setString(2, pBloque.getHashAnterior());
	            pstmt.setLong(3, pBloque.getMarcaTemporal());
	            pstmt.setInt(4, pBloque.getNonce());
	            if( pBloque.getTransacciones().size() != 0)
	            	pstmt.setString(5, pBloque.getTransacciones().get(0).getIDtransaccion());
	            else
	            	pstmt.setString(5, "");
	            if(pBloque.getContratos().size() != 0)
	            	pstmt.setString(6, pBloque.getContratos().get(0).getIDsmartContract());
	            else
	            	pstmt.setString(6, "");
	            pstmt.setString(7, pBloque.getContratoConfirmado());
	            pstmt.setString(8, pBloque.getContratoEjecutado());
	            pstmt.executeUpdate();
	            pstmt.close();
	            conn.close();
	        } catch (SQLException e) {}
	    }
	 
	 public static void insertarTransaccion(Transaccion pTran) throws Exception {
	        String sql = "INSERT INTO transaccion(IDtran, remitente, receptor, valor, firma, secuencia) VALUES(?,?,?,?,?,?)";
	        
	        try (Connection conn =  connect();
	                PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, pTran.getIDtransaccion());
	            pstmt.setString(2, StringUtils.getStringClave(pTran.getRemitente()));
	            pstmt.setString(3, StringUtils.getStringClave(pTran.getReceptor()));
	            pstmt.setFloat(4, pTran.getValor());
	            pstmt.setBytes(5, pTran.getFirma());
	            pstmt.setInt(6, pTran.getSecuencia());
	            pstmt.executeUpdate();
	            pstmt.close();
	            conn.close();
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	 
	 public static boolean comprobarCartera(String pClavePublica) {
		 String clave = null;
		 String sql = "SELECT clavePublica FROM cartera WHERE clavePublica = '"+ pClavePublica +"';";
	   	 
	        try (Connection conn =  connect();
	             PreparedStatement stmt  = conn.prepareStatement(sql);
	             ResultSet rs    = stmt.executeQuery()){
	        	 while (rs.next()) {
	        		clave = rs.getString("clavePublica");
	        	 }
	        	 rs.close();
	        	 stmt.close();
	             conn.close();
	             
	        } catch (SQLException se) {
	        	//  System.out.println(se.getMessage());
	        } 
	    
	    if(clave == null)
	    	return false;
	    else
	    	return true;
	 }
	 
	 public static ArrayList<Bloque> getBloques() throws Exception {
		 	String sql = "SELECT * FROM bloque;";
		 	Bloque b;
		 	ArrayList<Bloque> blockchain = new ArrayList<Bloque>();
		 	ArrayList<Transaccion> transacciones = new ArrayList<Transaccion>();
		 	ArrayList<SmartContract> contratos = new ArrayList<SmartContract>();
	   	 
		        try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		b = new Bloque();
		        		if(blockchain.size() == 0) {
		        			b.setHashAnterior("0");
		        		}
		        		else {
		        			b.setHashAnterior(blockchain.get(blockchain.size() - 1).getHash());
		        		}
		        			b.setHash(rs.getString("hash"));
		        			b.setMarcaTemporal(rs.getInt("marcaTemporal"));
		        			b.setNonce(rs.getInt("nonce"));
		        			if(!rs.getString("transaccion").equals("")) {
		        				transacciones.add(getTransaccion(rs.getString("transaccion")));
		        				b.setTransacciones(transacciones);
		        			}
		        			if(!rs.getString("contrato").equals("")) {
		        				contratos.add(getContrato(rs.getString("contrato")));
		        				b.setContratos(contratos);
		        	 		}
		        			b.setContratoConfirmado(rs.getString("contratoConfirmado"));
		        			b.setContratoEjecutado(rs.getString("contratoEjecutado"));
		        			blockchain.add(b);
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {}
		        
		    return blockchain;
	    }

		public static Transaccion getTransaccion(String pIDtransaccion) {
			String sql = "SELECT * FROM transaccion WHERE IDtran = '" + pIDtransaccion + "';";
			Transaccion t = null;
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		t  = new Transaccion();
		        		t.setIDtransaccion(rs.getString("IDtran"));
		        		t.setRemitente((PublicKey) StringUtils.getClaveDesdeString(rs.getString("remitente"), true));
		        		t.setReceptor((PublicKey) StringUtils.getClaveDesdeString(rs.getString("receptor"), true));
		        		t.setValor(rs.getFloat("valor"));
		        		t.setFirma(rs.getBytes("firma"));
		        		t.setSecuencia(rs.getInt("secuencia"));
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//  System.out.println(se.getMessage());
		        }

			return t;
		}
		
		public static SmartContract getContrato(String pID) {
			String sql = "SELECT * FROM smartContract WHERE IDsc = '" + pID + "';";
			SmartContract sc = null;
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		sc  = new SmartContract();
		        		sc.setIDsmartContract(rs.getString("IDsc"));
		        		sc.setFecha(rs.getInt("Fecha"));
		        		sc.setCantidad(rs.getInt("Cantidad"));
		        		sc.setPK_remitente(rs.getString("Remitente"));
		        		sc.setPK_receptor(rs.getString("Receptor"));
		        		sc.setFirmaTransaccion(rs.getBytes("FirmaTransaccion"));
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {}

			return sc;
		}
		
		public static ArrayList<Transaccion> getTransacciones() {
			String sql = "SELECT * FROM transaccion;";
			ArrayList<Transaccion> transacciones = new ArrayList<Transaccion>();
			Transaccion t; 
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		t = new Transaccion();
		        		t.setIDtransaccion(rs.getString("IDtran"));
		        		t.setRemitente((PublicKey) StringUtils.getClaveDesdeString(rs.getString("remitente"), true));
		        		t.setReceptor((PublicKey) StringUtils.getClaveDesdeString(rs.getString("receptor"), true));
		        		t.setValor(rs.getFloat("valor"));
		        		t.setFirma(rs.getBytes("firma"));
		        		t.setSecuencia(rs.getInt("secuencia"));
		        		transacciones.add(t);
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//  System.out.println(se.getMessage());
		        }

			return transacciones;
		}
		
		public static Transaccion getTranGenesis() {
			String sql = "SELECT * FROM transaccion WHERE rowid = 1;";
			Transaccion t = new Transaccion();
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		t.setIDtransaccion(rs.getString("IDtran"));
		        		t.setRemitente((PublicKey) StringUtils.getClaveDesdeString(rs.getString("remitente"), true));
		        		t.setReceptor((PublicKey) StringUtils.getClaveDesdeString(rs.getString("receptor"), true));
		        		t.setValor(rs.getFloat("valor"));
		        		t.setFirma(rs.getBytes("firma"));
		        		t.setSecuencia(rs.getInt("secuencia"));
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//  System.out.println(se.getMessage());
		        }

			return t;
		}

		public static void insertarContrato(String ID, String pK_receptor, int cantidad, String pK_remitente, long marcaTemp, byte[] pFirma) {
			
			String sql = "INSERT INTO smartContract(IDsc, Fecha, Cantidad, Remitente, Receptor, FirmaTransaccion) VALUES(?,?,?,?,?,?)";
	        
	        try (Connection conn =  connect();
	                PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, ID);
	            pstmt.setLong(2, marcaTemp);
	            pstmt.setInt(3, cantidad);
	            pstmt.setString(4, pK_remitente);
	            pstmt.setString(5, pK_receptor);
	            pstmt.setBytes(6, pFirma);
	            pstmt.executeUpdate();
	            pstmt.close();
	            conn.close();
	        } catch (SQLException e) {
	            //System.out.println(e.getMessage());
	        }
		}
		
		public static ArrayList<SmartContract> getContratosBD() {
			String sql = "SELECT * FROM smartContract;";
			ArrayList<SmartContract> contratos = new ArrayList<SmartContract>();
			SmartContract sc; 
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		sc = new SmartContract();
		        		sc.setIDsmartContract(rs.getString("IDsc"));
		        		sc.setPK_remitente(rs.getString("Remitente"));
		        		sc.setPK_receptor(rs.getString("Receptor"));
		        		sc.setFecha(rs.getLong("Fecha"));
		        		sc.setCantidad(rs.getInt("Cantidad"));
		        		sc.setFirmaTransaccion(rs.getBytes("FirmaTransaccion"));
		        		contratos.add(sc);
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//System.out.println(se.getMessage());
		        }

			return contratos;
		}
		
		public static void borrarContrato(String pID) throws Exception {
			 String sql = "DELETE FROM smartContract WHERE IDsc='"+pID+"';";
	    	 
		        try (Connection conn =  connect();
		                PreparedStatement pstmt = conn.prepareStatement(sql)) {
		 
		            // execute the delete statement
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
		        } catch (SQLException e) {
		        	//  System.out.println(e.getMessage());
		        }
		 }
		
		public static boolean existePK(String pID) {
			String sql = "SELECT Remitente, Receptor FROM smartContract WHERE IDsc ='"+ pID +"';";
			String remitente = ""; 
			String receptor = "";
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		remitente = rs.getString("Remitente");
		        		receptor = rs.getString("Receptor");
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//System.out.println(se.getMessage());
		        }

			if(recuperarPK(remitente) && recuperarPK(receptor))
				return true;
			else
				return false;
		}
		
		public static boolean recuperarPK(String pClave) {
			
			String sql = "SELECT clavePublica FROM cartera WHERE clavePublica ='"+ pClave +"';";
			String clave = null;
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		clave = rs.getString("clavePublica");
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {
		        	//System.out.println(se.getMessage());
		        }

			if(clave != null)
				return true;
			else
				return false;
		}
		
		public static ArrayList<String> getNombreRemitentes() {
			String sql = "SELECT usuario FROM smartContract LEFT JOIN cartera ON smartContract.Remitente = cartera.clavePublica";
			ArrayList<String> nombres = new ArrayList<String>();
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 nombres.add(rs.getString("usuario"));
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        	} catch (SQLException se) {}
			 
			 return nombres;
		}
		
		public static String getNombreUsuario(String pClavePublica) {
			String sql = "SELECT usuario FROM cartera WHERE clavePublica='" + pClavePublica + "';";
			String nombre = "";
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 nombre = rs.getString("usuario");
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        	} catch (SQLException se) {}
			 
			 return nombre;
		}
		
		public static ArrayList<String> rellenarTablaRemitente() {
			
			String sql = "SELECT usuario, Fecha, Cantidad, IDsc FROM smartContract INNER JOIN cartera ON smartContract.Receptor = cartera.clavePublica;";
			ArrayList<String> lista = new ArrayList<String>();
			ArrayList<String> remitentes = getNombreRemitentes();
			Integer i = 0; //Numero del contrato
			int j = 0;
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		i = i+1;
		        		lista.add(i.toString());
		        		String receptor = rs.getString("usuario");
		        		receptor = receptor.substring(0, 1).toUpperCase() + receptor.substring(1);
		        		lista.add(receptor);
		        		
		        		String remitente = remitentes.get(j).substring(0, 1).toUpperCase() + remitentes.get(j).substring(1);
		        		lista.add(remitente);
		        		j = j+1;
		        		
		        		long marcaTemp;
		        		marcaTemp = rs.getLong("Fecha");
		                DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm"); 
		                Date fecha = new Date(marcaTemp); 
		        		lista.add(simple.format(fecha));
		        		
		        		Integer c = rs.getInt("Cantidad");
		        		lista.add(c.toString() + " monedas");
		        		
		        		//Hay que coger el ID aunque no se muestre, para poder borrar el contrato.
		        		lista.add(rs.getString("IDsc"));
		        	 }	        	 
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        } catch (SQLException se) {}
			 
			 return lista;
		}

		public static boolean haSidoConfirmado(String pIDsmartContract) {
			String sql = "SELECT contratoConfirmado FROM bloque WHERE contrato='" + pIDsmartContract + "';";
			ArrayList<String> conf = new ArrayList<String>();
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 conf.add(rs.getString("contratoConfirmado"));
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        	} catch (SQLException se) {}
			 
			 boolean confirmado = false;
			 for(String c: conf) {
				 if(c.equals("true"))
					 confirmado = true;
			 }
			 return confirmado;
		}
		
		public static boolean haSidoEjecutado(String pIDsmartContract) {
			String sql = "SELECT contratoEjecutado FROM bloque WHERE contrato='" + pIDsmartContract + "';";
			ArrayList<String> ejec = new ArrayList<String>();
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 ejec.add(rs.getString("contratoEjecutado"));
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        	} catch (SQLException se) {}
			 
			 boolean ejecutado = true;
			 for(String c: ejec) {
				 if(c.equals("false"))
					 ejecutado = false;
			 }
			 return ejecutado;
		}
		
		public static ArrayList<String> contratosPendientes(String pReceptor) {
			String sql = "SELECT contrato, remitente, receptor, fecha, cantidad FROM bloque INNER JOIN smartContract ON bloque.contrato = smartContract.IDsc WHERE bloque.contratoConfirmado = 'false' AND smartContract.Receptor = '" + pReceptor + "';";
			ArrayList<String> datos = new ArrayList<String>();
			
			 try (Connection conn =  connect();
		             PreparedStatement stmt  = conn.prepareStatement(sql);
		             ResultSet rs    = stmt.executeQuery()){
		        	 while (rs.next()) {
		        		 datos.add(rs.getString("contrato"));
		        		 datos.add(rs.getString("remitente"));
		        		 datos.add(rs.getString("receptor"));
		        		 datos.add(rs.getString("fecha"));
		        		 datos.add(rs.getString("cantidad"));
		        	 }
		        	 rs.close();
		        	 stmt.close();
		             conn.close();
		        	} catch (SQLException se) {}
			 
			 return datos;
		}
} 
