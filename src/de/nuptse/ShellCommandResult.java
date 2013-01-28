package de.nuptse;

class ShellCommandResult {
    private final int mExitCode;
    private final String mOutput;

    public ShellCommandResult(int exitCode, String output) {
        mExitCode = exitCode;
        mOutput = output;
    }

    public int getExitCode() {
        return mExitCode;
    }

    public String getOutput() {
        return mOutput;
    }
}