package org.sanjoy.uitest.steps;
import java.util.ArrayList;
import java.util.List;


public class ExecutionStep {

	private String _method;
	protected List<Object> _parms = new ArrayList<Object>();

	public ExecutionStep() {

	}

	public String getMethod() {
		return _method;
	}

	public void setMethod(String method) {
		_method = method;
	}

	public void addParm(Object parm) {
		_parms.add(parm);
	}

	public List<Object> getParms() {
		return _parms;
	}

	public void dump() {
		System.err.println("Method : " + _method);
		if (_parms.size() != 0 ) {
			System.err.print(" parms : ");
			for (Object parm : _parms) System.err.print(parm + " , ");
			System.err.println();
		}
	}
}
