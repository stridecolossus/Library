package org.sarge.lib.inject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.stream.Stream;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.StreamUtil;
import org.sarge.lib.util.Util;

/**
 * Class-path scanner.
 * @author Sarge
 */
public class PackageScanner {
    private final ClassLoader loader;
    
    public PackageScanner(ClassLoader loader) {
        this.loader = Check.notNull(loader);
    }
    
    public PackageScanner() {
        this(Thread.currentThread().getContextClassLoader());
    }
    
    public Stream<Class<?>> scan(String name) throws IOException {
        final Enumeration<URL> resources = loader.getResources(name.replace('.', '/'));
        final Iterator<URL> itr = Util.iterator(resources);
        return StreamUtil.toStream(itr)
            .map(PackageScanner::toFile)
            .flatMap(file -> scan(file, name));
    }
    
    private static File toFile(URL url) {
        try {
            return new File(url.toURI());
        }
        catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Stream<Class<?>> scan(File dir, String parent) {
        for(File file : dir.listFiles()) {
            
        }
        
        
        
        try {
            Files.list(dir.toPath())
                    
                    
                    .flatMap(path -> scanFile(path.toFile(), parent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static Stream<Class<?>> scanFile(File file, String parent) {        
        final String name = file.getName();
        if(file.isDirectory()) {
            // Recurse to sub-directories
            return scan(file, parent + "." + name);
        }
        else
        if(file.getName().endsWith(".class")) {
            // Load class
            final String trimmed = name.substring(0, name.length() - 6);
            final Class<?> clazz;
            try {
                clazz = Class.forName(name + "." + trimmed);
            }
            catch(Exception e) {
                throw new RuntimeException("Error loading class: " + name, e);
            }
            return Stream.of(clazz);
        }
        else {
            return Stream.empty();
        }
    }
}
