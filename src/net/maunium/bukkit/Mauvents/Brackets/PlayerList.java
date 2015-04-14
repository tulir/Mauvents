package net.maunium.bukkit.Mauvents.Brackets;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PlayerList implements Iterable<UUID> {
	private UUID[] players;
	
	public PlayerList(int length) {
		players = new UUID[length];
	}
	
	public boolean remove(UUID u) {
		int i = indexOf(u);
		if (i != -1) players[i] = null;
		else return false;
		return true;
	}
	
	public int indexOf(UUID u) {
		if (u == null) {
			for (int i = 0; i < players.length; i++)
				if (players[i] == null) return i;
		} else {
			for (int i = 0; i < players.length; i++)
				if (u.equals(players[i])) return i;
		}
		return -1;
	}
	
	public boolean isEmpty() {
		if (arrayLength() == 0) return true;
		for (UUID u : players)
			if (u != null) return false;
		return true;
	}
	
	public boolean add(UUID u) {
		int i = indexOf(null);
		if (i != -1) players[i] = u;
		else return false;
		return true;
	}
	
	public boolean contains(UUID u) {
		return indexOf(u) != -1;
	}
	
	public int size() {
		int size = 0;
		for (int i = 0; i < players.length; i++)
			if (players[i] != null) size++;
		return size;
	}
	
	public int arrayLength() {
		return players.length;
	}
	
	@Override
	public Iterator<UUID> iterator() {
		return new ArrayIterator<UUID>(players);
	}
	
	public class ArrayIterator<T> implements Iterator<T> {
		private T array[];
		private int pos = 0;
		
		public ArrayIterator(T anArray[]) {
			array = anArray;
		}
		
		@Override
		public boolean hasNext() {
			return pos < array.length;
		}
		
		@Override
		public T next() throws NoSuchElementException {
			if (hasNext()) return array[pos++];
			else throw new NoSuchElementException();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
