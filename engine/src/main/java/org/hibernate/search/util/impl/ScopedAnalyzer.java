/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010-2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.search.util.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;

/**
 * A <code>ScopedAnalyzer</code> is a wrapper class containing all analyzers for a given class.
 * <code>ScopedAnalyzer</code> behaves similar to <code>PerFieldAnalyzerWrapper</code> by delegating requests for
 * <code>TokenStream</code>s to the underlying <code>Analyzer</code> depending on the requested field name.
 *
 * @author Emmanuel Bernard
 * @author Sanne Grinovero
 */
public final class ScopedAnalyzer extends AnalyzerWrapper {

	private Analyzer globalAnalyzer;
	private final Map<String, Analyzer> scopedAnalyzers = new HashMap<String, Analyzer>();

	/**
	 * @deprecated let's try to make at least the default analyzer mandatory
	 */
	@Deprecated
	public ScopedAnalyzer() {
		this( null );
	}

	public ScopedAnalyzer(Analyzer globalAnalyzer) {
		this( globalAnalyzer, Collections.<String, Analyzer>emptyMap() );
	}

	private ScopedAnalyzer(Analyzer globalAnalyzer, Map<String, Analyzer> scopedAnalyzers) {
		super( PER_FIELD_REUSE_STRATEGY );
		this.globalAnalyzer = globalAnalyzer;
		for ( Map.Entry<String, Analyzer> entry : scopedAnalyzers.entrySet() ) {
			addScopedAnalyzer( entry.getKey(), entry.getValue() );
		}
	}

	public void setGlobalAnalyzer(Analyzer globalAnalyzer) {
		this.globalAnalyzer = globalAnalyzer;
	}

	public void addScopedAnalyzer(String scope, Analyzer scopedAnalyzer) {
		scopedAnalyzers.put( scope, scopedAnalyzer );
	}

	@Override
	protected Analyzer getWrappedAnalyzer(String fieldName) {
		final Analyzer analyzer = scopedAnalyzers.get( fieldName );
		if ( analyzer == null ) {
			return globalAnalyzer;
		}
		else {
			return analyzer;
		}
	}

	@Override
	public ScopedAnalyzer clone() {
		ScopedAnalyzer clone = new ScopedAnalyzer( globalAnalyzer, scopedAnalyzers );
		return clone;
	}

}
