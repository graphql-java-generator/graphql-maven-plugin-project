/**
 * 
 */
package com.graphql_java_generator.plugin.test.compiler;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.slf4j.Logger;

/**
 * 
 * 
 * @author etienne-sf
 */
public class DiagnosticListenerImpl implements DiagnosticListener<JavaFileObject> {

	/** The Logger, where diagnostic report must be written */
	Logger log;
	/**
	 * The class being compiler, which compiling is 'listened' to, by the current instance. Null if not applicable, for
	 * instance if several classes are compiled at once
	 */
	String className = null;

	/**
	 * @param log
	 */
	public DiagnosticListenerImpl(Logger log) {
		this.log = log;
	}

	/**
	 * @param log
	 * @param className
	 */
	public DiagnosticListenerImpl(Logger log, String className) {
		this.log = log;
		this.className = className;
	}

	/** @see javax.tools.DiagnosticListener#report(javax.tools.Diagnostic) */
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		// In debug mode, we show all messages, but in standard mode we show only Errors
		// and Mand
		if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)
				|| diagnostic.getKind().equals(Diagnostic.Kind.MANDATORY_WARNING)) {
			if (log.isErrorEnabled()) {
				log.error(getDiagnostic(diagnostic));
			}
		} else if (diagnostic.getKind().equals(Diagnostic.Kind.WARNING)
				|| diagnostic.getKind().equals(Diagnostic.Kind.MANDATORY_WARNING)) {
			if (log.isWarnEnabled()) {
				log.warn(getDiagnostic(diagnostic));
			}
		} else if (diagnostic.getKind().equals(Diagnostic.Kind.NOTE)) {
			if (log.isInfoEnabled()) {
				log.info(getDiagnostic(diagnostic));
			}
		} else if (log.isDebugEnabled()) {
			// Other kind are only displayed when in debug mode
			log.debug(getDiagnostic(diagnostic));
		}
	}

	/**
	 * Actual log of the received {@link Diagnostic}
	 * 
	 * @param diagnostic
	 */
	String getDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
		StringBuilder msg = new StringBuilder();
		JavaFileObject source = diagnostic.getSource();

		msg.append(diagnostic.getKind().toString());
		if (className != null) {
			msg.append(" in the generated class ").append(className);
		}
		msg.append(": ");
		msg.append(diagnostic.getCode()).append(" at line ").append(diagnostic.getLineNumber()).append(", column ")
				.append(diagnostic.getColumnNumber()).append(": ");
		msg.append(diagnostic.getMessage(null)).append(" (in ").append((source == null) ? "null" : source.getName());

		return msg.toString();
	}
}
