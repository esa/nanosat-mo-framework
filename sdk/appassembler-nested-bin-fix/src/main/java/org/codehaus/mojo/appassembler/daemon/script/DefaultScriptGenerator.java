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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.codehaus.mojo.appassembler.daemon.DaemonGeneratorException;
import org.codehaus.mojo.appassembler.model.Daemon;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultScriptGenerator
    extends AbstractLogEnabled
    implements ScriptGenerator
{

    private static final String DEFAULT_LICENSE_HEADER = "default-license-header.txt";

    private boolean isDefaultLicenseHeaderRequested( Daemon daemon )
    {
        if ( daemon.getLicenseHeaderFile() == null )
        {
            return true;
        }

        if ( daemon.getLicenseHeaderFile().trim().length() > 0 )
        {
            return false;
        }

        return false;
    }

    private String getLicenseHeader( Platform platform, Daemon daemon )
        throws DaemonGeneratorException
    {
        List<String> lines;
        if ( isDefaultLicenseHeaderRequested( daemon ) )
        {
            getLogger().debug( "Using default licence file (" + DEFAULT_LICENSE_HEADER + ")." );
            lines = readLicenseHeader();
        }
        else
        {
            getLogger().debug( "Using license file: " + daemon.getLicenseHeaderFile() );
            lines = readLicenseHeaderFromFile( new File( daemon.getLicenseHeaderFile() ) );
        }
        StringBuilder resultLines = new StringBuilder();
        for ( int i = 0; i < lines.size(); i++ )
        {
            String licenseLine = platform.getCommentPrefix() + lines.get( i );
            resultLines.append( licenseLine.trim() + platform.getNewLine() );
        }
        return resultLines.toString();
    }

    private List<String> readLicenseHeader()
        throws DaemonGeneratorException
    {
        ArrayList<String> result = new ArrayList<String>();

        InputStream in = getClass().getResourceAsStream( DEFAULT_LICENSE_HEADER );

        InputStreamReader inr = new InputStreamReader( in );
        BufferedReader bufRead = null;
        try
        {
            bufRead = new BufferedReader( inr );
            String str;
            while ( ( str = bufRead.readLine() ) != null )
            {
                result.add( str );
            }
        }
        catch ( IOException e )
        {
            throw new DaemonGeneratorException(
                                                "Internal error: could not read license header template file (license-header.txt)" );
        }
        finally
        {
            try {
                bufRead.close();
            } catch (IOException e) {
                // swallow
            }
        }
        return result;
    }

    private List<String> readLicenseHeaderFromFile( File licenseHeader )
        throws DaemonGeneratorException
    {
        ArrayList<String> result = new ArrayList<String>();
        try
        {
            BufferedReader in = new BufferedReader( new FileReader( licenseHeader ) );
            String str;
            while ( ( str = in.readLine() ) != null )
            {
                result.add( str );
            }
            in.close();
        }
        catch ( IOException e )
        {
            throw new DaemonGeneratorException( "Internal error: could not read license header template file "
                + licenseHeader.getName() );
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // ScriptGenerator Implementation
    // -----------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.appassembler.daemon.script.ScriptGenerator#createBinScript(java.lang.String,
     * org.codehaus.mojo.appassembler.model.Daemon, java.io.File, java.lang.String)
     */
    public void createBinScript( String platformName, Daemon daemon, File outputDirectory, String binFolder )
        throws DaemonGeneratorException
    {
        Platform platform = Platform.getInstance( platformName );

        InputStream in = null;

        FileWriter out = null;
        File binFile;

        try
        {
            in = getScriptTemplate( platformName, daemon );

            InputStreamReader reader = new InputStreamReader( in, "UTF-8" );
            File binDir = new File( outputDirectory, binFolder );
            String binToBaseDirRelativePath = binDir.toPath().relativize( outputDirectory.toPath() ).toString();

            Map<Object, Object> context = new HashMap<Object, Object>();
            context.put( "MAINCLASS", daemon.getMainClass() );
            context.put( "CLASSPATH", platform.getClassPath( daemon ) );
            context.put( "EXTRA_JVM_ARGUMENTS", platform.getExtraJvmArguments( daemon.getJvmSettings() ) );
            context.put( "APP_NAME", daemon.getId() );
            context.put( "ENV_SETUP", platform.getEnvSetup( daemon, binFolder ) );
            context.put( "REPO", daemon.getRepositoryName() );
            context.put( "LICENSE_HEADER", getLicenseHeader( platform, daemon ) );
            context.put( "BIN_BASE_RELATIVE_PATH", platform.convertPathSeparators( binToBaseDirRelativePath ) );

            if ( daemon.getEndorsedDir() != null )
            {
                context.put( "ENDORSED_DIR", daemon.getEndorsedDir() );
            }
            else
            {
                context.put( "ENDORSED_DIR", "" );
            }

            if ( platform.isShowConsoleWindow( daemon ) )
            {
                context.put( "JAVA_BINARY", "java" );
                context.put( "UNIX_BACKGROUND", "" );
            }
            else
            {
                context.put( "JAVA_BINARY", "start /min javaw" );
                context.put( "UNIX_BACKGROUND", " &" );
            }

            String appArguments = platform.getAppArguments( daemon );
            if ( appArguments != null )
            {
                context.put( "APP_ARGUMENTS", appArguments + " " );
            }
            else
            {
                context.put( "APP_ARGUMENTS", "" );
            }

            String interpolationToken = platform.getInterpolationToken();
            InterpolationFilterReader interpolationFilterReader =
                new InterpolationFilterReader( reader, context, interpolationToken, interpolationToken );

            // Set the name of the bin file
            String programName = "";

            if ( daemon.getId() == null || daemon.getId().trim().equals( "" ) )
            {
                // Get class name and use it as the filename
                StringTokenizer tokenizer = new StringTokenizer( daemon.getMainClass(), "." );
                while ( tokenizer.hasMoreElements() )
                {
                    programName = tokenizer.nextToken();
                }

                programName = programName.toLowerCase();
            }
            else
            {
                programName = daemon.getId();
            }

            FileUtils.forceMkdir( binDir );
            binFile = new File( binDir, programName + platform.getBinFileExtension() );
            if ( Platform.UNIX_NAME.equals( platformName ) && binFile.exists() )
            {
                // Only in case of an existing file it does make sense
                try
                {
                    // in case it already exists, make it writable. Maybe deleting would be better?
                    ArchiveEntryUtils.chmod( binFile, 0777, getLogger(), true );
                }
                catch ( ArchiverException ae )
                {
                    throw new DaemonGeneratorException( "Failed to change permission for bin file.", ae );
                }
            }

            out = new FileWriter( binFile );
            getLogger().debug( "Writing shell file for platform '" + platform.getName() + "' to '"
                                   + binFile.getAbsolutePath() + "'." );

            IOUtil.copy( interpolationFilterReader, out );
        }
        catch ( FileNotFoundException e )
        {
            throw new DaemonGeneratorException( "Failed to get template for bin file.", e );
        }
        catch ( IOException e )
        {
            throw new DaemonGeneratorException( "Failed to write bin file.", e );
        }
        finally
        {
            IOUtil.close( out );
            IOUtil.close( in );
        }

        if ( Platform.UNIX_NAME.equals( platformName ) )
        {
            try
            {
                // TODO: The permissions should be made configurable.
                ArchiveEntryUtils.chmod( binFile, 0755, getLogger(), true );
            }
            catch ( ArchiverException ae )
            {
                throw new DaemonGeneratorException( "Failed to change permission for bin file.", ae );
            }
        }
    }

    private InputStream getScriptTemplate( String platformName, Daemon daemon )
        throws DaemonGeneratorException
    {
        InputStream is = null;

        try
        {
            String customTemplate = daemon.getWindowsScriptTemplate();
            if ( Platform.UNIX_NAME.equals( platformName ) )
            {
                customTemplate = daemon.getUnixScriptTemplate();
            }

            if ( customTemplate != null )
            {
                File customTemplateFile = new File( customTemplate );
                if ( customTemplateFile.exists() )
                {
                    is = new FileInputStream( customTemplateFile );
                }
                else
                {
                    is = getClass().getClassLoader().getResourceAsStream( customTemplate );
                    if ( is == null )
                    {
                        throw new DaemonGeneratorException( "Unable to load external template resource: "
                            + customTemplate );
                    }
                }
            }
            else
            {
                is = getClass().getResourceAsStream( platformName + "BinTemplate" );
                if ( is == null )
                {
                    throw new DaemonGeneratorException( "Unable to load internal template resource: " + platformName
                        + "BinTemplate" );
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            throw new DaemonGeneratorException( "Unable to load external template file", e );
        }

        return is;

    }
}
