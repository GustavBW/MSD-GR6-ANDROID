package gbw.sdu.msd.backend.models;

public enum DebtGraphExitCode {
    SELF_DEBT(false),
    TRANSFERRED(true),
    DIRECT(true);

    public final boolean success;
    DebtGraphExitCode(boolean isError){
        this.success = isError;
    }
}
