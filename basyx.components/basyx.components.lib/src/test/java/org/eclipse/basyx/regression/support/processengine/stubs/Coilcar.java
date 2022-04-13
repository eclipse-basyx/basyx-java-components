/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.stubs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coilcar implements ICoilcar {

	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Coilcar.class);

	private int currentPosition = 0;
	private int currentLifterPosition = 0;

	@Override
	public int moveTo(int position) {
		logger.debug("#submodel# invoke service +MoveTo+ with parameter: %d \n\n", position);
		Double steps[] = generateCurve(currentPosition, position);
		for (Double step : steps) {
			logger.debug(step == null ? "null" : step.toString());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		currentPosition = position;
		return currentPosition;
	}

	@Override
	public int liftTo(int position) {
		logger.debug("#submodel# Call service LiftTo with Parameter: %d \n\n", position);
		Double steps[] = generateCurve(currentLifterPosition, position);
		for (Double step : steps) {
			logger.debug(step == null ? "null" : step.toString());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		currentLifterPosition = position;
		return currentLifterPosition;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public int getCurrentLifterPosition() {
		return currentLifterPosition;
	}

	private Double[] generateCurve(double current, double goal) {
		Double stepList[] = new Double[20];
		double delta = (goal - current) / 20;
		for (int i = 0; i < 20; i++) {
			stepList[i] = current + delta * (i + 1);
		}
		return stepList;
	}

}
