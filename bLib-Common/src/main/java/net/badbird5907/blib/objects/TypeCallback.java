package net.badbird5907.blib.objects;

import java.io.Serializable;

public interface TypeCallback<T, B> extends Serializable {

	/**
	 * A callback for running a task on a set of data.
	 *
	 * @param data the data needed to run the task.
	 */
	T callback(B data);

}