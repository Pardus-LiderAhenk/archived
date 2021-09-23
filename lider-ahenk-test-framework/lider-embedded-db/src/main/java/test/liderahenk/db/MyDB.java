package test.liderahenk.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.vorburger.exec.ManagedProcess;
import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;

public class MyDB extends DB {

	private Method[] declaredMethods;
	private Field declaredField;
	Class noparams[] = {};


	protected MyDB(DBConfiguration config) {
		super(config);
		dbStartMaxWaitInMS = 15000;
	}
	
	
//	@Override
//	public synchronized void start() throws ManagedProcessException {
//		
//		try {
//			
//			Method declaredMethod = this.getClass().getSuperclass().getDeclaredMethod("startPreparation", noparams);
//			declaredMethod.setAccessible(true);
//			Object invoke = declaredMethod.invoke(this);
//			
//			
//			declaredField = this.getClass().getSuperclass().getDeclaredField("mysqldProcess");
//			System.out.println(declaredField.getType());
//			declaredField.setAccessible(true);
//			declaredField.set(this, invoke);
//			
//			
//			Object old = declaredField.get(this);
//			
//			Method myMethod =old.getClass().getDeclaredMethod("start", new Class[]{});
//			myMethod.setAccessible(true);
//			myMethod.invoke(old);
//			
//		}  catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} 
//		
//	}
	
	
	public static MyDB newMyDb(DBConfiguration config) throws ManagedProcessException{
		MyDB mydb = new MyDB(config);
		mydb.prepareDirectories();
		mydb.unpackEmbeddedDb();
		mydb.install();
		return mydb;
	}
	
	
	
	
	

}
