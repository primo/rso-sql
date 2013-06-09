package pw.edu.elka.rso.logic.procedures; /**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 28.05.13
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */

import net.sf.jsqlparser.JSQLParserException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.out;

public class ProceduresManager{
    //singleton
    private static ProceduresManager instance = null;

    static final String proceduresPackageName = "pw.edu.elka.rso.procedures";
    ArrayList<Procedure> procedures = new ArrayList<Procedure>();

    public ProceduresManager() {
        // Exists only to defeat instantiation.
    }
    public static ProceduresManager getInstance() {
        if(instance == null) {
            instance = new ProceduresManager();
        }
        return instance;
    }
    /*
    * method to execute stored procedure
    * @param procedure - name of procedure to execute
    * TODO return data type ???
     */
    public String executeProcedure(String procedure, List<String> params) throws ClassNotFoundException, JSQLParserException {
        //TODO change to some structure to find by name, like associative array
        Iterator<Procedure> it = procedures.iterator();
        while(it.hasNext())
        {
            Procedure p = it.next();
            if(p.name.equals(procedure)){
                return p.run(params);
                //return p.run();
            }
        }

        return null;
    }

  public Procedure getProcedure(String procedure,List<String> params) throws ClassNotFoundException {
    //TODO change to some structure to find by name, like associative array
    Iterator<Procedure> it = procedures.iterator();
    while(it.hasNext())
    {
      Procedure p = it.next();
      if(p.name.equals(procedure)){
        p.prepareParameters(params);
        return p;
        //return p.run();
      }
    }

    return null;
  }


  /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @return The classes names
     */
    public ArrayList getProceduresNames(){
        ArrayList names = new ArrayList();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;

        String path = proceduresPackageName.replace('.', '/');

        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            System.out.println("Wrong package name! Not found!");
            System.exit(901);
        }
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            names.addAll(findClasses(directory, proceduresPackageName));
        }

        return names;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes names ArrayList
     */
    private static ArrayList findClasses(File directory, String packageName)  {
        ArrayList classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(file.getName().substring(0, file.getName().length() - 6));
            }
        }
        return classes;
    }

    /**
     * Method used to prepare all procedures before system start
     */
    public void prepareProcedures() {
        ArrayList proceduresNames = getProceduresNames();
        Iterator<String> it = proceduresNames.iterator();
        while(it.hasNext())
        {
            String name = it.next();
            Class<?> c = null;
            try {
                c = Class.forName(proceduresPackageName+"."+name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Object t = c.newInstance();
                Method runMethod = c.getDeclaredMethod("prepareProcedure");
                out.print("Preparing "+name+" procedure...");
                runMethod.setAccessible(true);
                Procedure p = (Procedure)runMethod.invoke(t);

                procedures.add(p);
                out.println("done");

            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchMethodException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
