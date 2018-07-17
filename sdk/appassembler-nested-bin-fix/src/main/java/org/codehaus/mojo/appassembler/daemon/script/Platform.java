package org.codehaus.mojo.appassembler.daemon.script;

/*
 * The MIT License
 *
 * Copyright (c) 2006-2012, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;

import org.codehaus.mojo.appassembler.daemon.DaemonGeneratorException;
import org.codehaus.mojo.appassembler.model.ClasspathElement;
import org.codehaus.mojo.appassembler.model.Daemon;
import org.codehaus.mojo.appassembler.model.Dependency;
import org.codehaus.mojo.appassembler.model.Directory;
import org.codehaus.mojo.appassembler.model.JvmSettings;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Platform
{
    /**
     * Unix as Platform name.
     */
    public static final String UNIX_NAME = "unix";

    /**
     * Windows Platform name.
     */
    public static final String WINDOWS_NAME = "windows";

    private static final Map<String, Platform> ALL_PLATFORMS;

    private static final String DEFAULT_UNIX_BIN_FILE_EXTENSION = "";

    private static final String DEFAULT_WINDOWS_BIN_FILE_EXTENSION = ".bat";

    private String binFileExtension;

    private String name;

    private boolean isWindows;

    // -----------------------------------------------------------------------
    // Static
    // -----------------------------------------------------------------------

    static
    {
        ALL_PLATFORMS = new HashMap<String, Platform>();
        addPlatform( new Platform( UNIX_NAME, false, DEFAULT_UNIX_BIN_FILE_EXTENSION ) );
        addPlatform( new Platform( WINDOWS_NAME, true, DEFAULT_WINDOWS_BIN_FILE_EXTENSION ) );
    }

    private static Platform addPlatform( Platform platform )
    {
        ALL_PLATFORMS.put( platform.name, platform );

        return platform;
    }

    /**
     * Get an instance of the named platform.
     *
     * @param platformName The name of the wished platform.
     * @return Instance of the platform.
     * @throws DaemonGeneratorException in case of an wrong platformname.
     */
    public static Platform getInstance( String platformName )
        throws DaemonGeneratorException
    {
        Platform platform = ALL_PLATFORMS.get( platformName );

        if ( platform == null )
        {
            throw new DaemonGeneratorException( "Unknown platform name '" + platformName + "'" );
        }

        return platform;
    }

    /**
     * Get the names of all available platforms.
     *
     * @return The names of the platform.
     */
    public static Set<String> getAllPlatformNames()
    {
        return ALL_PLATFORMS.keySet();
    }

    /**
     * Get all platforms.
     *
     * @return All platforms.
     */
    public static Set<Platform> getAllPlatforms()
    {
        return new HashSet<Platform>( ALL_PLATFORMS.values() );
    }

    /**
     * Redefine the list of platforms with the given one.
     *
     * @param platformList The new list of platforms.
     * @return The redefined platforms set.
     * @throws DaemonGeneratorException in case of an error.
     */
    public static Set<Platform> getPlatformSet( List<String> platformList )
        throws DaemonGeneratorException
    {
        return getPlatformSet( platformList, new HashSet<Platform>( ALL_PLATFORMS.values() ) );
    }

    /**
     * Get back all platforms.
     *
     * @param platformList The list of platforms.
     * @param allSet The all set list.
     * @return Get the platform sets.
     * @throws DaemonGeneratorException in case of an error.
     */
    public static Set<Platform> getPlatformSet( List<String> platformList, Set<Platform> allSet )
        throws DaemonGeneratorException
    {
        if ( platformList == null )
        {
            return allSet;
        }

        if ( platformList.size() == 1 )
        {
            String first = platformList.get( 0 );

            if ( "all".equals( first ) )
            {
                return allSet;
            }

            throw new DaemonGeneratorException(
                                                "The special platform 'all' can only be used if it is the only element in the platform list." );
        }

        Set<Platform> platformSet = new HashSet<Platform>();

        for ( String platformName : platformList )
        {
            if ( platformName.equals( "all" ) )
            {
                throw new DaemonGeneratorException(
                                                    "The special platform 'all' can only be used if it is the only element in a platform list." );
            }

            platformSet.add( getInstance( platformName ) );
        }

        return platformSet;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private Platform( String name, boolean isWindows, String binFileExtension )
    {
        this.name = name;

        this.isWindows = isWindows;

        this.binFileExtension = binFileExtension;
    }

    // -----------------------------------------------------------------------
    // The platform-specific bits
    // -----------------------------------------------------------------------

    /**
     * The interpolation token either for windows or unix.
     *
     * @return The token which is used.
     */
    public String getInterpolationToken()
    {
        return isWindows ? "#" : "@";
    }

    /**
     * @return The binary extension.
     */
    public String getBinFileExtension()
    {
        return binFileExtension;
    }

    /**
     * @return BASEDIR representation for windows or unix.
     */
    public String getBasedir()
    {
        return isWindows ? "\"%BASEDIR%\"" : "\"$BASEDIR\"";
    }

    /**
     * @return REPO representation for windows or unix.
     */
    public String getRepo()
    {
        return isWindows ? "\"%REPO%\"" : "\"$REPO\"";
    }

    /**
     * @return The separator for windows or unix.
     */
    public String getSeparator()
    {
        return isWindows ? "\\" : "/";
    }

    /**
     * @return The path separator for windows or unix.
     */
    public String getPathSeparator()
    {
        return isWindows ? ";" : ":";
    }

    /**
     * @return Comment prefix for windows or unix.
     */
    public String getCommentPrefix()
    {
        return isWindows ? "@REM " : "# ";
    }

    public String getNewLine()
    {
        return isWindows ? "\r\n" : "\n";
    }

    // -----------------------------------------------------------------------
    // This part depend on the platform-specific parts
    // -----------------------------------------------------------------------

    /**
     * Get the ClassPath based on the given Daemon.
     *
     * @param daemon The Daemon instance.
     * @return The classpath as a string.
     * @throws DaemonGeneratorException in case of an error.
     */
    public String getClassPath( Daemon daemon )
        throws DaemonGeneratorException
    {
        List<ClasspathElement> classpath = daemon.getAllClasspathElements();

        StringBuilder classpathBuffer = new StringBuilder();

        for ( ClasspathElement classpathElement : classpath )
        {
            if ( classpathBuffer.length() > 0 )
            {
                classpathBuffer.append( getPathSeparator() );
            }

            // -----------------------------------------------------------------------
            //
            // -----------------------------------------------------------------------

            if ( classpathElement instanceof Directory )
            {
                Directory directory = (Directory) classpathElement;

                if ( directory.getRelativePath().charAt( 0 ) != '/' )
                {
                    classpathBuffer.append( getBasedir() ).append( getSeparator() );
                }
            }
            else if ( classpathElement instanceof Dependency )
            {
                classpathBuffer.append( getRepo() ).append( getSeparator() );
            }
            else
            {
                throw new DaemonGeneratorException( "Unknown classpath element type: "
                    + classpathElement.getClass().getName() );
            }

            classpathBuffer.append( StringUtils.replace( classpathElement.getRelativePath(), "/", getSeparator() ) );
        }

        return classpathBuffer.toString();
    }

    private String interpolateBaseDirAndRepo( String content )
    {
        StringReader sr = new StringReader( content );
        StringWriter result = new StringWriter();

        Map<Object, Object> context = new HashMap<Object, Object>();

        context.put( "BASEDIR", StringUtils.quoteAndEscape( getBasedir(), '"' ) );
        context.put( "REPO", StringUtils.quoteAndEscape( getRepo(), '"' ) );
        InterpolationFilterReader interpolationFilterReader = new InterpolationFilterReader( sr, context, "@", "@" );
        try
        {
            IOUtil.copy( interpolationFilterReader, result );
        }
        catch ( IOException e )
        {
            // shouldn't happen...
        }
        return result.toString();
    }

    private List<String> convertArguments( List<String> strings )
    {
        if ( strings == null )
        {
            return strings;
        }

        ArrayList<String> result = new ArrayList<String>();
        for ( String argument : strings )
        {
            result.add( interpolateBaseDirAndRepo( argument ) );
        }

        return result;
    }

    /**
     * Get the extra JVMArguments.
     *
     * @param jvmSettings The JVM settings
     * @return The created string which contains <code>-X</code> options for the JVM settings.
     * @throws IOException in case of an error.
     */
    public String getExtraJvmArguments( JvmSettings jvmSettings )
        throws IOException
    {
        if ( jvmSettings == null )
        {
            return "";
        }

        String vmArgs = "";

        vmArgs = addJvmSetting( "-Xms", jvmSettings.getInitialMemorySize(), vmArgs );
        vmArgs = addJvmSetting( "-Xmx", jvmSettings.getMaxMemorySize(), vmArgs );
        vmArgs = addJvmSetting( "-Xss", jvmSettings.getMaxStackSize(), vmArgs );

        vmArgs += arrayToString( convertArguments( jvmSettings.getExtraArguments() ), "" );
        vmArgs += arrayToString( jvmSettings.getSystemProperties(), "-D" );

        return vmArgs.trim();
    }

    private String arrayToString( List<String> strings, String separator )
    {
        String string = "";

        if ( strings != null )
        {
            for ( String s : strings )
            {
                if ( s.indexOf( ' ' ) == -1 )
                {
                    string += " " + separator + s;
                }
                else
                {
                    string += " \"" + separator + s + "\"";
                }
            }
        }

        return string;
    }

    /**
     * Get the application arguments.
     *
     * @param descriptor Instance of the daemon descriptor.
     * @return The list of application arguments.
     */
    public String getAppArguments( Daemon descriptor )
    {
        List<String> commandLineArguments = convertArguments( descriptor.getCommandLineArguments() );

        if ( commandLineArguments == null || commandLineArguments.isEmpty() )
        {
            return null;
        }

        if ( commandLineArguments.size() == 1 )
        {
            return commandLineArguments.get( 0 );
        }

        Iterator<String> it = commandLineArguments.iterator();

        String appArguments = it.next();

        while ( it.hasNext() )
        {
            appArguments += " " + it.next();
        }

        return appArguments;
    }

    private String addJvmSetting( String argType, String extraJvmArgument, String vmArgs )
    {
        if ( StringUtils.isEmpty( extraJvmArgument ) )
        {
            return vmArgs;
        }

        return vmArgs + " " + argType + extraJvmArgument;
    }

    /**
     * Get the environment setup file.
     *
     * @param daemon The instance of the Daemon for which this is beeing produced.
     * @return The created string which contains the path to the setup file.
     */
    public String getEnvSetup( Daemon daemon, String binFolder )
    {
        String envSetup = "";

        String envSetupFileName = daemon.getEnvironmentSetupFileName();

        if ( envSetupFileName != null )
        {
            String targetPlatformBinFolder = convertPathSeparators( binFolder );
            if ( isWindows )
            {
                String envScriptPath = "\"%BASEDIR%\\" + targetPlatformBinFolder + "\\" + envSetupFileName + ".bat\"";

                envSetup = "if exist " + envScriptPath + " call " + envScriptPath;
            }
            else
            {
                String envScriptPath = "\"$BASEDIR\"/" + targetPlatformBinFolder + "/" + envSetupFileName;
                envSetup = "[ -f " + envScriptPath + " ] && . " + envScriptPath;
            }
        }

        return envSetup;
    }

    /**
     * Converts path separators (slashes) into the target platform format.
     *
     * @param path Input path (system-independent)
     * @return Path with separators converted into the target platform format.
     */
    public String convertPathSeparators( String path )
    {
        if ( isWindows )
        {
            return FilenameUtils.separatorsToWindows( path );
        }
        else
        {
            return FilenameUtils.separatorsToUnix( path );
        }
    }

    // -----------------------------------------------------------------------
    // Object overrides
    // -----------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Platform platform = (Platform) o;

        return name.equals( platform.name );
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return name.hashCode();
    }

    /**
     * The name of the platform.
     *
     * @return The name of the platform.
     */
    public String getName()
    {
        return name;
    }

    /**
     * ShowConsole window.
     *
     * @param daemon
     * @return true yes false otherwise.
     */
    public boolean isShowConsoleWindow( Daemon daemon )
    {
        return daemon.isShowConsoleWindow();
    }

    // -----------------------------------------------------------------------
    // Setters for the platform-specific bits
    // -----------------------------------------------------------------------

    /**
     * Set the bin file extension.
     *
     * @param binFileExtension The extension of the binary file.
     */
    public void setBinFileExtension( String binFileExtension )
    {
        // We can't have a null extension
        if ( binFileExtension == null )
        {
            this.binFileExtension = "";
        }
        else
        {
            this.binFileExtension = binFileExtension;
        }
    }
}
