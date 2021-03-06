package org.apache.lucene.store;



import java.io.IOException;

/*
  Java's filesystem API is not used directly, but rather through these
  classes.  This permits:
    . implementation of RAM-based indices, useful for summarization, etc.;
    . implementation of an index as a single file.

*/

/**
  A Directory is a flat list of files.  Files may be written once,
  when they are created.  Once a file is created it may only be opened for
  read, or deleted.  Random access is permitted when reading and writing.

    @author Doug Cutting
*/

abstract public class Directory {
  /** Returns an array of strings, one for each file in the directory. */
  abstract public String[] list()
       throws IOException, SecurityException;
       
  /** Returns true iff a file with the given name exists. */
  abstract public boolean fileExists(String name)
       throws IOException, SecurityException;

  /** Returns the time the named file was last modified. */
  abstract public long fileModified(String name)
       throws IOException, SecurityException;

  /** Removes an existing file in the directory. */
  abstract public void deleteFile(String name)
       throws IOException, SecurityException;

  /** Renames an existing file in the directory.
    If a file already exists with the new name, then it is replaced.
    This replacement should be atomic. */
  abstract public void renameFile(String from, String to)
       throws IOException, SecurityException;

  /** Returns the length of a file in the directory. */
  abstract public long fileLength(String name)
       throws IOException, SecurityException;

  /** Creates a new, empty file in the directory with the given name.
      Returns a stream writing this file. */
  abstract public OutputStream createFile(String name)
       throws IOException, SecurityException;

  /** Returns a stream reading an existing file. */
  abstract public InputStream openFile(String name)
       throws IOException, SecurityException;

  /** Closes the store. */
  abstract public void close()
       throws IOException, SecurityException;
}
