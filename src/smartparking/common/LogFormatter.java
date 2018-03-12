package smartparking.common;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by mirco on 19/05/17.
 */
public class LogFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		return record.getLoggerName()+": "+formatMessage(record)+"\n";
	}
}
