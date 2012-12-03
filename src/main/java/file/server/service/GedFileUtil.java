package file.server.service;


/**
 * Atenção: Por padrão o caminho para os arquivos é o home do usuário /files.
 *
 * 
 * 
 * @author josecoelho
 *
 */
public class GedFileUtil {

	/**
	 * Calculates the path by db id
	 * and returns it.
	 * @param id 
	 * @return String path
	 */
	public static String getPathById(Long id) {
		
		//NIVEL 1 = id / 256 
        double nivel_1_double = id / Math.pow(256, 4);
        int nivel_1_int = (int) nivel_1_double;
		
		//NIVEL 2 = id / 256 
        double nivel_2_double = id / Math.pow(256, 3);
        int nivel_2_int = (int) nivel_1_double;
        
        //NIVEL 3 = id / 256 
        double nivel_3_double = id / Math.pow(256, 2);
        int nivel_3_int = (int) nivel_2_double;
        
        //NIVEL 4 = id / 256 AND 255
        int nivel_4_int = (int) (id / 256) & 255;
        
        String path = String.format("%03d", nivel_1_int) 
        		+ "/" + String.format("%03d", nivel_2_int) 
        		+ "/" + String.format("%03d", nivel_3_int) 
        		+ "/"  + String.format("%03d", nivel_4_int) 
        		+ "/" + String.format("%010d", id);
       
		return System.getProperty("user.home")+"/files/"+path;
	}
	
}
