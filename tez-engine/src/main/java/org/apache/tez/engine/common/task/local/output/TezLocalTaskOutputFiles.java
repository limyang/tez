/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.tez.engine.common.task.local.output;

import java.io.IOException;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalDirAllocator;
import org.apache.hadoop.fs.Path;
import org.apache.tez.common.Constants;
import org.apache.tez.common.TezJobConfig;
import org.apache.tez.engine.records.TezTaskID;

/**
 * Manipulate the working area for the transient store for maps and reduces.
 *
 * This class is used by map and reduce tasks to identify the directories that
 * they need to write to/read from for intermediate files. The callers of
 * these methods are from the Child running the Task.
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TezLocalTaskOutputFiles extends TezTaskOutput {

  private LocalDirAllocator lDirAlloc =
    new LocalDirAllocator(TezJobConfig.LOCAL_DIR);

  public TezLocalTaskOutputFiles() {
  }

  /**
   * Return the path to local map output file created earlier
   *
   * @return path
   * @throws IOException
   */
  @Override
  public Path getOutputFile()
      throws IOException {
    return lDirAlloc.getLocalPathToRead(Constants.TASK_OUTPUT_DIR + Path.SEPARATOR
        + Constants.TEZ_ENGINE_TASK_OUTPUT_FILENAME_STRING, getConf());
  }

  /**
   * Create a local map output file name.
   *
   * @param size the size of the file
   * @return path
   * @throws IOException
   */
  @Override
  public Path getOutputFileForWrite(long size)
      throws IOException {
    return lDirAlloc.getLocalPathForWrite(Constants.TASK_OUTPUT_DIR + Path.SEPARATOR
        + Constants.TEZ_ENGINE_TASK_OUTPUT_FILENAME_STRING, size, getConf());
  }

  /**
   * Create a local map output file name on the same volume.
   */
  @Override
  public Path getOutputFileForWriteInVolume(Path existing) {
    return new Path(existing.getParent(), Constants.TEZ_ENGINE_TASK_OUTPUT_FILENAME_STRING);
  }

  /**
   * Return the path to a local map output index file created earlier
   *
   * @return path
   * @throws IOException
   */
  @Override
  public Path getOutputIndexFile()
      throws IOException {
    return lDirAlloc.getLocalPathToRead(Constants.TASK_OUTPUT_DIR + Path.SEPARATOR
        + Constants.TEZ_ENGINE_TASK_OUTPUT_FILENAME_STRING + Constants.TEZ_ENGINE_TASK_OUTPUT_INDEX_SUFFIX_STRING,
        getConf());
  }

  /**
   * Create a local map output index file name.
   *
   * @param size the size of the file
   * @return path
   * @throws IOException
   */
  @Override
  public Path getOutputIndexFileForWrite(long size)
      throws IOException {
    return lDirAlloc.getLocalPathForWrite(Constants.TASK_OUTPUT_DIR + Path.SEPARATOR
        + Constants.TEZ_ENGINE_TASK_OUTPUT_FILENAME_STRING + Constants.TEZ_ENGINE_TASK_OUTPUT_INDEX_SUFFIX_STRING,
        size, getConf());
  }

  /**
   * Create a local map output index file name on the same volume.
   */
  @Override
  public Path getOutputIndexFileForWriteInVolume(Path existing) {
    return new Path(existing.getParent(),
        Constants.TEZ_ENGINE_TASK_OUTPUT_FILENAME_STRING + Constants.TEZ_ENGINE_TASK_OUTPUT_INDEX_SUFFIX_STRING);
  }

  /**
   * Return a local map spill file created earlier.
   *
   * @param spillNumber the number
   * @return path
   * @throws IOException
   */
  @Override
  public Path getSpillFile(int spillNumber)
      throws IOException {
    return lDirAlloc.getLocalPathToRead(Constants.TASK_OUTPUT_DIR + "/spill"
        + spillNumber + ".out", getConf());
  }

  /**
   * Create a local map spill file name.
   *
   * @param spillNumber the number
   * @param size the size of the file
   * @return path
   * @throws IOException
   */
  @Override
  public Path getSpillFileForWrite(int spillNumber, long size)
      throws IOException {
    return lDirAlloc.getLocalPathForWrite(Constants.TASK_OUTPUT_DIR + "/spill"
        + spillNumber + ".out", size, getConf());
  }

  /**
   * Return a local map spill index file created earlier
   *
   * @param spillNumber the number
   * @return path
   * @throws IOException
   */
  @Override
  public Path getSpillIndexFile(int spillNumber)
      throws IOException {
    return lDirAlloc.getLocalPathToRead(Constants.TASK_OUTPUT_DIR + "/spill"
        + spillNumber + ".out.index", getConf());
  }

  /**
   * Create a local map spill index file name.
   *
   * @param spillNumber the number
   * @param size the size of the file
   * @return path
   * @throws IOException
   */
  @Override
  public Path getSpillIndexFileForWrite(int spillNumber, long size)
      throws IOException {
    return lDirAlloc.getLocalPathForWrite(Constants.TASK_OUTPUT_DIR + "/spill"
        + spillNumber + ".out.index", size, getConf());
  }

  /**
   * Return a local reduce input file created earlier
   *
   * @param mapId a map task id
   * @return path
   * @throws IOException
   */
  @Override
  public Path getInputFile(int mapId)
      throws IOException {
    return lDirAlloc.getLocalPathToRead(String.format(
        Constants.TEZ_ENGINE_TASK_INPUT_FILE_FORMAT_STRING, 
        Constants.TASK_OUTPUT_DIR, Integer.valueOf(mapId)), getConf());
  }

  /**
   * Create a local reduce input file name.
   *
   * @param mapId a map task id
   * @param size the size of the file
   * @return path
   * @throws IOException
   */
  @Override
  public Path getInputFileForWrite(TezTaskID mapId,
                                   long size)
      throws IOException {
    return lDirAlloc.getLocalPathForWrite(String.format(
        Constants.TEZ_ENGINE_TASK_INPUT_FILE_FORMAT_STRING, Constants.TASK_OUTPUT_DIR, mapId.getId()),
        size, getConf());
  }

  /** Removes all of the files related to a task. */
  @Override
  public void removeAll()
      throws IOException {
    deleteLocalFiles(Constants.TASK_OUTPUT_DIR);
  }

  @Override
  public void setConf(Configuration conf) {
    super.setConf(conf);
  }

  private String[] getLocalDirs() throws IOException {
    return getConf().getStrings(TezJobConfig.LOCAL_DIR);
  }

  @SuppressWarnings("deprecation")
  private void deleteLocalFiles(String subdir) throws IOException {
    String[] localDirs = getLocalDirs();
    for (int i = 0; i < localDirs.length; i++) {
      FileSystem.getLocal(getConf()).delete(new Path(localDirs[i], subdir));
    }
  }

}
