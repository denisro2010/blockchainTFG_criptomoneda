package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import blockchain.StringUtils;
 
public class databaseControl {
    

	public static Connection connect() {
	   
	        Connection conn = null;
	        try {     
	            String url = "jdbc:sqlite:blockchainTFG.db";
	            conn = DriverManager.getConnection(url);
	            
	          //  System.out.println("La conexión a la base de datos se ha realizado con éxito.");
	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        } finally {
	            try {
	                if (conn != null) {
	                    conn.close();
	                }
	            } catch (SQLException ex) {
	                System.out.println(ex.getMessage());
	            }

	        }
			return conn;
	}
	
	 public static void tablaBloque() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS bloque (hash STRING PRIMARY KEY NOT NULL, hashAnterior STRING NOT NULL UNIQUE, marcaTemporal BIGINT NOT NULL, nonce INTEGER NOT NULL, merkleRoot STRING, transaccion STRING REFERENCES transaccion (IDtran) NOT NULL);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	            System.out.println(e.getMessage());
	         }
	    }
	 
	 public static void tablaCartera() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS cartera (usuario STRING NOT NULL UNIQUE PRIMARY KEY, contrasena STRING NOT NULL, clavePublica STRING NOT NULL UNIQUE, clavePrivada STRING NOT NULL);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	            System.out.println(e.getMessage());
	         }
	    }
	 
	 public static void tablaOutputs() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE IF NOT EXISTS outputs (IDoutput STRING PRIMARY KEY NOT NULL UNIQUE, pkey_receptor STRING NOT NULL, cantidad DOUBLE NOT NULL, IDtransaccion STRING NOT NULL, IDcartera REFERENCES cartera (clavePublica) NOT NULL);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	            System.out.println(e.getMessage());
	         }
	    }
	 
	 public static void tablaTransaccion() throws Exception {
	    	
	    	String sqlUsers = "CREATE TABLE transaccion (IDtran STRING PRIMARY KEY, remitente STRING NOT NULL, receptor STRING NOT NULL, valor DOUBLE NOT NULL, firma STRING NOT NULL, secuencia INTEGER);";
	    	
	    	 try (Connection conn = connect();
	    	    Statement stmt = conn.createStatement()){
	    	    stmt.executeUpdate(sqlUsers);
	    	    stmt.close();
	    	    conn.close();
	    	    
	    	 } catch (SQLException e) {
	            System.out.println(e.getMessage());
	         }
	    }
	 
	 public static void crearCartera(String pUsuario, String pContra, String pClavePublica, String pClavePrivada) throws Exception {
	        String sql = "INSERT INTO cartera(usuario, contrasena, clavePublica, clavePrivada) VALUES(?,?,?,?)";
	 
	        String contraHash = StringUtils.applySha256(pContra);
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pUsuario);
		            pstmt.setString(2, contraHash);
		            pstmt.setString(3, pClavePublica);
		            pstmt.setString(4, pClavePrivada);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	 
	 public static void crearTransaccion(String pIDtran, String pRemitente, String pReceptor, float pValor, String pFirma, int pSecuencia) throws Exception {
	        String sql = "INSERT INTO transaccion(IDtran, remitente, receptor, valor, firma, secuencia) VALUES(?,?,?,?,?,?)";
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pIDtran);
		            pstmt.setString(2, pRemitente);
		            pstmt.setString(3, pReceptor);
		            pstmt.setFloat(4, pValor);
		            pstmt.setString(5, pFirma);
		            pstmt.setInt(6, pSecuencia);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	 
	 public static void crearBloque(String pHash, String pHashAnterior, long pMarcaTemp, int pNonce, String pMerkleRoot, String pTransaccion) throws Exception {
	        String sql = "INSERT INTO bloque(hash, hashAnterior, marcaTemporal, nonce, merkleRoot, transaccion) VALUES(?,?,?,?,?,?)";
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pHash);
		            pstmt.setString(2, pHashAnterior);
		            pstmt.setLong(3, pMarcaTemp);
		            pstmt.setInt(4, pNonce);
		            pstmt.setString(5, pMerkleRoot);
		            pstmt.setString(6, pTransaccion);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	 
	 public static void crearOutput(String pID, String pReceptor, float pCant, String pIDtran) throws Exception {
	        String sql = "INSERT INTO outputs(IDoutput, pkey_receptor, cantidad, IDtransaccion) VALUES(?,?,?,?)";
	        
	        try (Connection conn =  connect();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        	
		            pstmt.setString(1, pID);
		            pstmt.setString(2, pReceptor);
		            pstmt.setDouble(3, pCant);
		            pstmt.setString(4, pIDtran);
		            pstmt.executeUpdate();
		            pstmt.close();
		            conn.close();
		            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
    
	/* 
    public static void cifrarContras() throws Exception {
    	String nomUser = UserActual.getNombreUser();
    	String masterPass = UserActual.getMasterPassUser();
    	String sql = "update contrasenas set passSitio = AES_ENCRYPT(:passSitio,'"+masterPass+"') WHERE usuario='"+nomUser+"';";
    	try (Connection conn =  ConnectorMXJObject.main();
	        PreparedStatement pst = conn.prepareStatement(sql)) {
	        pst.executeUpdate();
	        pst.close();
	        conn.close();
    	}
	     catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
    }
 

    public static void insertUser(int pDia, int pMes, int pAnio, String pNombre, String pPass, int pLongitud) throws Exception {
        String sql = "INSERT INTO usuarios(dia, mes, anio, nombre, pass, longitud) VALUES(?,?,?,?,?,?)";
 
        pPass = Hash.aplicarSha512(pPass);
        
        try (Connection conn =  ConnectorMXJObject.main();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pDia);
            pstmt.setInt(2, pMes);
            pstmt.setInt(3, pAnio);
            pstmt.setString(4, pNombre);
            pstmt.setString(5, pPass);
            pstmt.setInt(6, pLongitud);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void insertPass(String nomSitio, String pass) throws Exception {
    	String masterPass = UserActual.getMasterPassUser();
        String sql = "INSERT INTO contrasenas(nombreSitio, passSitio, usuario) VALUES(?, AES_ENCRYPT(?, '"+ masterPass +"') ,?)";
        
        try (Connection conn =  ConnectorMXJObject.main();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, nomSitio);
        	pstmt.setString(2, pass);
            pstmt.setString(3, UserActual.getNombreUser()); //!!!
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void deleteAllUsr() throws Exception {
        String sql = "DELETE FROM usuarios";
 
        try (Connection conn =  ConnectorMXJObject.main();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // execute the delete statement
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }
    }
    
    public static void deleteAllPass(String nomUser) throws Exception {
        String sql = "DELETE FROM contrasenas WHERE usuario='" + nomUser + "';" ;
 
        try (Connection conn =  ConnectorMXJObject.main();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // execute the delete statement
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }
    }
    
    public static void deleteOnePass(String value) throws Exception {
    	String masterPass = UserActual.getMasterPassUser();
        String sql = "DELETE FROM contrasenas WHERE passSitio = AES_ENCRYPT(?, '"+ masterPass +"')";
 
        try (Connection conn =  ConnectorMXJObject.main();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // set the corresponding param
            pstmt.setString(1, value);
            // execute the delete statement
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }  
    
    public static void actualizaTablaContras() throws Exception {
    	 String masterPass = UserActual.getMasterPassUser();
    	 
    	 String sql2 = "SELECT nombreSitio,  CAST(AES_DECRYPT(passSitio, '"+ masterPass +"') AS CHAR(255)) AS passSitio FROM contrasenas WHERE usuario='"+UserActual.getNombreUser()+"';";
	       
    	 	try (Connection conn = ConnectorMXJObject.main();
	             PreparedStatement stmt  = conn.prepareStatement(sql2);
	             ResultSet rs    = stmt.executeQuery()){
	        	 VentanaContrasEN.getTable().setModel(DbUtils.resultSetToTableModel(rs));
	        	 rs.close();
	        	 stmt.close();
	             conn.close();
	        } catch (SQLException se) {
	            System.out.println(se.getMessage());
	        }
    }
    
    public static String getMasterPass(String nomUser) throws Exception {
   	 String masterPass=null;
   	 String sql2 = "SELECT pass FROM usuarios WHERE nombre='" + nomUser + "';";
   	 
	        try (Connection conn =  ConnectorMXJObject.main();
	             PreparedStatement stmt  = conn.prepareStatement(sql2);
	             ResultSet rs    = stmt.executeQuery()){
	        	 while (rs.next()) {
	        		 masterPass = rs.getString("pass");	
	        	 }
	        	 rs.close();
	        	 stmt.close();
	             conn.close();
	        } catch (SQLException se) {
	            System.out.println(se.getMessage());
	        }
	        return masterPass;
   }
    
    public static void deleteOneUser() throws Exception {
    	String masterPass = UserActual.getMasterPassUser();
    	String sql = "DELETE FROM usuarios WHERE nombre='"+UserActual.getNombreUser()+"' and pass='"+Hash.aplicarSha512(masterPass)+"';";
    	 
        try (Connection conn =  ConnectorMXJObject.main();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // execute the delete statement
            pstmt.executeUpdate();
            deleteUserPasswords();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }
      }
    
    private static void deleteUserPasswords() throws Exception {
    	String sql = "DELETE FROM contrasenas WHERE usuario='"+UserActual.getNombreUser()+"';";
    	
    	try (Connection conn =  ConnectorMXJObject.main();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // execute the delete statement
            pstmt.executeUpdate();
            
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }
    }
    
    public static boolean nameSeRepite(String nomUser) throws Exception {
      	 boolean seRepite = false;
      	 String sql2 = "SELECT nombre FROM usuarios";
      	 
   	        try (Connection conn =  ConnectorMXJObject.main();
   	             PreparedStatement stmt  = conn.prepareStatement(sql2);
   	             ResultSet rs    = stmt.executeQuery()){
   	        	 while (rs.next()) {
   	        		 if(rs.getString("nombre").equals(nomUser)) {
   	        			 seRepite = true;
   	        		 }
   	        	 }
   	        	 rs.close();
   	        	 stmt.close();
   	             conn.close();
   	        } catch (SQLException se) {
   	            System.out.println(se.getMessage());
   	        }
   	        return seRepite;
      }
    
    public static void setOtrosDatosUserActual(String nomUser, String masterPass) throws Exception {

      	 String sql2 = "SELECT * FROM usuarios WHERE nombre='" + nomUser + "' and pass ='" + Hash.aplicarSha512(masterPass) + "';";
      	 
   	        try (Connection conn =  ConnectorMXJObject.main();
   	             PreparedStatement stmt  = conn.prepareStatement(sql2);
   	             ResultSet rs    = stmt.executeQuery()){
   	        	 while (rs.next()) {
   	        		UserActual.setDiaNac(rs.getInt("dia")); 
   	        		UserActual.setMesNac(rs.getInt("mes")); 
   	        		UserActual.setAnioNac(rs.getInt("anio")); 
   	        		UserActual.setLongPass(rs.getInt("longitud"));  
   	        	 }
   	        	 rs.close();
   	        	 stmt.close();
   	             conn.close();
   	        } catch (SQLException se) {
   	            System.out.println(se.getMessage());
   	        }
    }
    
     */
    
} 
