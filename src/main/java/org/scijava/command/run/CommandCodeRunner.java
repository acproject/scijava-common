/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.command.run;

import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.run.AbstractCodeRunner;
import org.scijava.run.CodeRunner;

/**
 * Runs the given {@link Command} class.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = CodeRunner.class)
public class CommandCodeRunner extends AbstractCodeRunner {

	@Parameter
	private PluginService pluginService;

	@Parameter
	private CommandService commandService;

	// -- CodeRunner methods --

	@Override
	public void run(final Object code, final Object... args) {
		commandService.run(getCommandClass(code), true, args);
	}

	// -- Typed methods --

	@Override
	public boolean supports(final Object code) {
		return getCommandClass(code) != null;
	}

	// -- Helper methods --

	private Class<? extends Command> getCommandClass(final Object code) {
		if (!(code instanceof Class)) return null;
		final Class<?> c = (Class<?>) code;
		if (!Command.class.isAssignableFrom(c)) return null;
		@SuppressWarnings("unchecked")
		final Class<? extends Command> commandClass = (Class<? extends Command>) c;
		return commandClass;
	}

}
