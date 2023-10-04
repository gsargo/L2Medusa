package net.sf.l2j.gameserver.state;

public interface State<T>
{
	void handle(T context);
	
	void onStateStart(T context);
	
	void onStateFinish(T context);
	
	boolean canMoveToState(State<T> newState);
}