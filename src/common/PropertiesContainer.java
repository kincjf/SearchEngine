package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * method for reading 'config.properties'
 * @author KIMSEONHO
 *
 */
public class PropertiesContainer {
	public static Properties prop = null;
	public static enum PathType {ABSOLUTE, CLASSPATH};
	
	String path = "E:/data/config.properties";		// windows 기준;
			//path = "config.properties";
	/**
	 * default constructor
	 */
	public PropertiesContainer() {
		if(prop == null) {
			
			prop = new Properties();
			this.loadFile(PathType.ABSOLUTE);
		}
	}
	
	/**
	 * which to use? absolute path? classpath?
	 * @param pathType
	 */
	private void loadFile(PathType pathType) {
		switch(pathType) {
		case ABSOLUTE:
			this.loadAbsoluteFile();
			break;
		case CLASSPATH:
			this.loadClasspathFile();
			break;
		}
	}
	
	private void loadAbsoluteFile() {
		try {
			prop.load(new FileInputStream(path));
		} catch (IOException e) {
			System.out.println("PropertiesContainer SetProperties - loadFile err");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void loadClasspathFile() {
		try {
			//System.out.println("PropertiesContainer - loadClassPathFile " + PropertiesContainer.class.getPath());
			prop.load(PropertiesContainer.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			System.out.println("PropertiesContainer SetProperties - loadClasspathFile err");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
