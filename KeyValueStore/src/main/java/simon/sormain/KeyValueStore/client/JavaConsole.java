/**
 * This file is part of the ID2203 course assignments kit.
 * 
 * Copyright (C) 2009-2013 KTH Royal Institute of Technology
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package simon.sormain.KeyValueStore.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Start;

/**
 * 
 * Component provided in last year ID2203s assignments
 *
 */
public class JavaConsole extends ComponentDefinition implements Runnable {

	private Negative<ConsolePort> console = provides(ConsolePort.class);

	private final Thread thread;

	public JavaConsole() {
		thread = new Thread(this);
		thread.setDaemon(true);

		subscribe(handleStart, control);
		subscribe(handleOutput, console);
	}

	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			thread.start();
		}
	};

	private Handler<ConsoleLine> handleOutput = new Handler<ConsoleLine>() {
		@Override
		public void handle(ConsoleLine event) {
			System.out.println(event.getLine());
		}
	};

	public void run() {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(System.in));
		while (true) {
			try {
				String line = in.readLine();
				trigger(new ConsoleLine(line) , console);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
