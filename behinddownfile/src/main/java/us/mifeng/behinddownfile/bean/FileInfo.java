package us.mifeng.behinddownfile.bean;

/**
 * Created by 黑夜之火 on 2017/12/22.
 */

public class FileInfo {
    private long totalSize;
    private long downSize;
    private String name;
    private String path;
    private String filePath;
    private String fileId;

    public FileInfo(long totalSize, long downSize, String name, String path, String filePath, String fileId) {
        this.totalSize = totalSize;
        this.downSize = downSize;
        this.name = name;
        this.path = path;
        this.filePath = filePath;
        this.fileId = fileId;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getDownSize() {
        return downSize;
    }

    public void setDownSize(long downSize) {
        this.downSize = downSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
