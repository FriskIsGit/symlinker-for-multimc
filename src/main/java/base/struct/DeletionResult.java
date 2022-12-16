package base.struct;

public class DeletionResult{
    public int deletedFiles, deletedDirectories, failures;

    public DeletionResult(int deletedFiles, int deletedDirectories, int failures){
        this.deletedFiles = deletedFiles;
        this.deletedDirectories = deletedDirectories;
        this.failures = failures;
    }

    public DeletionResult(){}
}
