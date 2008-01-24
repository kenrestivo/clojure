/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
 *   which can be found in the file CPL.TXT at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jan 23, 2008 */

package clojure.lang;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Namespace{
final public Symbol name;
final AtomicReference<IPersistentMap> mappings = new AtomicReference<IPersistentMap>();

final static ConcurrentHashMap<Symbol, Namespace> namespaces = new ConcurrentHashMap<Symbol, Namespace>();

Namespace(Symbol name){
	this.name = name;
	mappings.set(RT.DEFAULT_IMPORTS);
}

public IPersistentMap getMappings(){
	return mappings.get();
}

Var intern(Symbol sym){
	if(sym.ns != null)
		{
		throw new IllegalArgumentException("Can't intern namespace-qualified symbol");
		}
	IPersistentMap map = getMappings();
	Object o;
	Var v = null;
	while((o = map.valAt(sym)) == null)
		{
		if(v == null)
			v = new Var(this, sym);
		map = getMappings();
		IPersistentMap newMap = map.assoc(sym,v);
		mappings.compareAndSet(map,newMap);
		}
	if(o instanceof Var && ((Var) o).ns == this)
		return (Var) o;

	throw new IllegalStateException(sym + " already refers to: " + o + " in namespace: " + name);
}

Var unintern(Var var){
   return null;
}

public Class importClass(Symbol sym, Class c){
	return null;

}

public Class unimport(Symbol sym){
	return null;

}


public Var refer(Symbol sym, Var var){
	return null;

}

public Var unrefer(Symbol sym){
	return null;

}

public static Namespace findOrCreate(Symbol name){
	Namespace ns = namespaces.get(name);
	if(ns != null)
		return ns;
	Namespace newns = new Namespace(name);
	ns = namespaces.putIfAbsent(name,newns);
	return ns == null?newns:ns;
}

public static Namespace find(Symbol name){
	return namespaces.get(name);
}

public Object getMapping(Symbol name){
	return mappings.get().valAt(name);
}

public Var findInternedVar(Symbol symbol){
	Object o = mappings.get().valAt(symbol);
	if(o != null && o instanceof Var && ((Var)o).ns == this)
		return (Var) o;
	return null;
}
}