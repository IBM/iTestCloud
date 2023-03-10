/*********************************************************************
 * Copyright (c) 2009, 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *********************************************************************/
package itest.cloud.utils;

import static itest.cloud.scenario.ScenarioUtils.DEBUG;
import static itest.cloud.scenario.ScenarioUtils.debugPrintln;

import java.io.*;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import itest.cloud.scenario.errors.ScenarioFailedError;

/**
 * Utilities to manipulate files and directories on disk through java.io
 * <p>
 * Note that this code has been initially copied from JFS team...
 * </p>
 */
public class FileUtil {

	private static final String EXTENSION_SEPARATOR = "\\.";
	private static final int BUFFER_SIZE = 8192;

/**
* Quick check that file exists; outputs to debug log if so.
*
* @param file File to check
* @throws ScenarioFailedError if the file does not exist.
*/
public static void check(final File file) throws ScenarioFailedError {
	if (file.exists()) {
		if (DEBUG) debugPrintln("File '" + file + "' exists at location '" + file.getAbsolutePath() + "'.");
	} else {
		throw new ScenarioFailedError("File '" + file + "' does not exist at location '" + file.getAbsolutePath() + "'.");
	}
}

/**
 * Compares the contents of two files.
 * <p>
 * This method checks whether the files exist and if there are directories.
 * Then, check whether the contents of the files is the same.
 * </p>
 *
 * @param file1 the first file
 * @param file2 the second file
 * @return <code>true</code> if content of the two files are equal or <code>false</code> otherwise.
 *
 * @throws IOException If an error occurs during the comparison.
 */
public static boolean contentEquals(final File file1, final File file2) throws IOException {
	return FileUtils.contentEquals(file1, file2);
}

/**
 * Copy all files from source directory to destination directory.
 *
 * @param sourceDir The source directory
 * @param destDir The destination directory
 * @throws IOException
 */
public static void copyDir(final File sourceDir, final File destDir)  throws IOException {
	if (!destDir.mkdirs())
		throw new IOException("Could not create " + destDir.getCanonicalPath()); //$NON-NLS-1$
	File[] sourceFiles = sourceDir.listFiles();
	if (sourceFiles == null) return;
	for (File sourceFile: sourceFiles) {
		if (sourceFile.isDirectory() ) {
			copyDir(sourceFile, new File(destDir, sourceFile.getName()));
		} else {
			copyFile(sourceFile, destDir);
		}
	}
}

/**
 * Copy the given source file to the given destination directory.
 *
 * @param sourceFile The file to copy
 * @param destDir The directory where to copy the file
 * @return The copied file as a {@link File}
 * @throws IOException
 */
public static File copyFile(final File sourceFile, final File destDir) throws IOException {
	return copyFile(sourceFile, destDir, null);
}

/**
 * Copy the given source file to the given destination directory with a different
 * name.
 *
 * @param sourceFile The file to copy
 * @param destDir The directory where to copy the file
 * @param destFile The new file name
 * @return The copied file as a {@link File}
 * @throws IOException
 */
public static File copyFile(final File sourceFile, final File destDir, final String destFile) throws IOException {
    InputStream inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
    String destFileName = destFile == null ? sourceFile.getName() : destFile;
    try {
		return createFile(destDir, destFileName, inputStream);
    } finally {
		try {
			inputStream.close();
		} catch (IOException e) {
			// don't throw this exception as it would hide any exception thrown in the try block
		}
    }
}

/**
 * Return the {@link File} corresponding to the given path.
 * <p>
 * If the directory does not exist, then it creates it.
 * </p>
 * @param dirPath The path of the directory
 * @return The {@link File} corresponding to the directory or <code>null</code>
 * if it didn't exist and that was not possible to create it.
 */
public static File createDir(final String dirPath) {
	File dir = new File(dirPath);
	if (!dir.exists() && !dir.mkdirs()) {
		System.err.println("Cannot create directory '"+dirPath+"'.");
		dir = null;
	}
	return dir;
}

/**
 * Return the {@link File} corresponding to the given path and the sub-directory.
 * <p>
 * If the directories do not exist, then it creates them.
 * </p>
 * @param dirPath The path of the directory
 * @param subdirName The sub-directory name
 * @return The {@link File} corresponding to the directory or <code>null</code>
 * if it didn't exist and that was not possible to create it.
 */
public static File createDir(final String dirPath, final String subdirName) {
	File dir = createDir(dirPath);
	if (dir != null) {
		File subdir = new File(dir, subdirName);
		if (subdir.exists() || subdir.mkdirs()) {
			return subdir;
		}
		System.err.println("Cannot create sub-directory '"+subdirName+"' in '"+dirPath+"'.");
	}
	return null;
}

private static File createFile(final File destDir, final String destFileName, final InputStream inputStream) throws FileNotFoundException, IOException {
	File destFile = new File(destDir, destFileName);
	OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile));
	try {
		read(inputStream, outputStream);
	} finally {
		try {
			outputStream.close();
		} catch (IOException e) {
			// don't throw this exception as it would hide any exception thrown in the try block
		}
	}
	return destFile;
}

/**
 * Delete the given file.
 * <p>
 * Note that if the deletion fails, it's performed again until success or five
 * consecutive failures.
 * </p>
 * @param file The file to delete
 * @throws IOException
 */
public static void deleteFile(final File file) throws IOException {
	for (int i = 1, maxAttempt = 5; i <= maxAttempt; i++) {
		if (file.delete()) break;
		if (i == maxAttempt) {
			throw new IOException("Could not delete " + file.getCanonicalPath()); //$NON-NLS-1$
		}
		try {
			Thread.sleep(250); // wait 250 ms before retrying
		} catch(InterruptedException e) {
			// ignore
		}
	}
}

/**
 * Return the extension of a given file.
 *
 * @param file The name of the file along with its extension.
 *
 * @return The extension of the given file as {@link String}.
 */
public static String getFileExtension(final String file) {
	String[] fileInfo = file.split(EXTENSION_SEPARATOR);

	return (fileInfo.length >= 2) ? fileInfo[fileInfo.length - 1] : null;
}

/**
 * Return the name of a given file without its extension.
 *
 * @param file The file.
 *
 * @return the name of a given file without its extension as {@link String}.
 */
public static String getFileName(final File file) {
	return file.getName().split(EXTENSION_SEPARATOR)[0];
}

/**
 * Specifies whether a given is a temporary file.
 * <p>
 * Such a file may be created during a download operation to collect the
 * downloaded content and discarded after the download has completed.
 * </p>
 *
 * @param file The corresponding file to check.
 *
 * @return <code>true</code> if the given is a temporary file or
 * <code>false</code> otherwise.
 */
public static boolean isTemporaryFile(final File file) {
	return Pattern.matches(".*\\.part|.*\\.crdownload|.*\\.Chrome\\..*", file.getName());
}

private static void read(final InputStream inputStream, final OutputStream outputStream) throws IOException {
	byte[] buffer = new byte[BUFFER_SIZE];
	int readSize = 0;
	while (true) {
		readSize = inputStream.read(buffer);
		if (readSize == -1)
			break;
		outputStream.write(buffer, 0, readSize);
	}
}

/**
 * Read the content of the gievn file, assuming it's a text file.
 *
 * @param file The file to read
 * @return The file content as a {@link String}.
 * @throws IOException
 */
public static String readFileContent(final File file) throws IOException {
    InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
    try {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	read(inputStream, outputStream);
    	byte[] bytes = outputStream.toByteArray();
    	outputStream.close();
    	return new String(bytes, "UTF-8");
    } finally {
		try {
			inputStream.close();
		} catch (IOException e) {
			// don't throw this exception as it would hide any exception thrown in the try block
		}
   }
}

/**
 * Delete an entire directory hierarchy  including all files.
 *
 * @param dir The directory to delete
 * @throws IOException
 */
public static void rmdir(final File dir) throws IOException {
	File[] files = dir.listFiles();
	if (files == null) return;
	for (File file: files) {
		if (file.isDirectory() ) {
			rmdir(file);
		} else {
			deleteFile(file);
		}
	}
	deleteFile(dir);
}

/**
 * Verify if a file exists at the given path. If the file doesn't exist yet,
 * wait. If not found within <b>timeout</b> seconds, returns false.
 *
 * @param filePath The path of the target file to verify for existence.
 * @return <b>true</b> if the given file exists, <b>false</b> otherwise.
 */
public static boolean waitUntilFileExists(final File filePath, final int timeout) {
	debugPrintln("		+ waiting for the file " + filePath.getAbsolutePath() + " to exist");
	long timeoutMilliseconds = timeout * 1000 + System.currentTimeMillis();
	while (!filePath.exists()) {
		if (System.currentTimeMillis() > timeoutMilliseconds) {
			return false;
		}
	}
	return true;
}

/**
 * Verify if a file exists at the given path. If the file doesn't exist yet,
 * wait. If not found within <b>timeout</b> seconds, returns false.
 *
 * @param path The absolute path of the target file to verify for existence.
 * @return <b>true</b> if the given file exists, <b>false</b> otherwise.
 */
public static boolean waitUntilFileExists(final String path, final int timeout) {
	return waitUntilFileExists(new File(path), timeout);
}
}
