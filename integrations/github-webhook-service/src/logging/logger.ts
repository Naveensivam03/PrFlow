type LogLevel = "INFO" | "WARN" | "ERROR";

type LogData = Record<string, unknown>;

function write(level: LogLevel, message: string, data?: LogData): void {
  const entry = {
    timestamp: new Date().toISOString(),
    level,
    message,
    ...data
  };
  const line = JSON.stringify(entry);
  if (level === "ERROR") {
    console.error(line);
    return;
  }
  console.log(line);
}

export const logger = {
  info: (message: string, data?: LogData) => write("INFO", message, data),
  warn: (message: string, data?: LogData) => write("WARN", message, data),
  error: (message: string, data?: LogData) => write("ERROR", message, data)
};
