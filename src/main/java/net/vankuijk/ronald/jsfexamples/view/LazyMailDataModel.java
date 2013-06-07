/*
 * Copyright 2009-2011 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vankuijk.ronald.jsfexamples.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.vankuijk.ronald.jsfexamples.domain.*;
import net.vankuijk.ronald.jsfexamples.domain.*;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Dummy implementation of LazyDataModel that uses a list to mimic a real datasource like a database.
 */
public class LazyMailDataModel extends LazyDataModel<Mail> {

	private List<Mail> datasource;

	public LazyMailDataModel(List<Mail> datasource) {
		this.datasource = datasource;
	}
	
    private Mail findData(String rowKey, Mail parent) {

    	for(Mail mail : parent.children) {
            if(mail.getId().equals(rowKey))
                return mail;
            else {
            	Mail descendant = findData(rowKey, mail);
				if (descendant != null) {
					return descendant;
				}
            }
        }

        return null;

    }

	@Override
	public Mail getRowData(String rowKey) {

		for (Mail mail : datasource) {
			if (rowKey.equals(getRowKey(mail)))
				return mail;
			else {
				Mail descendant = findData(rowKey, mail);
				if (descendant != null) {
					return descendant;
				}
			}

		}

		return null;

	}

	@Override
	public Object getRowKey(Mail mail) {
		return mail.getId();
	}

	@Override
	public List<Mail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		List<Mail> data = new ArrayList<Mail>();

		// filter
		for (Mail mail : datasource) {
			boolean match = true;

			for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
				try {
					String filterProperty = it.next();
					String filterValue = filters.get(filterProperty);
					String fieldValue = String.valueOf(mail.getClass().getField(filterProperty).get(mail));

					if (filterValue == null || fieldValue.startsWith(filterValue)) {
						match = true;
					} else {
						match = false;
						break;
					}
				} catch (Exception e) {
					match = false;
				}
			}

			if (match) {
				data.add(mail);
			}
		}

		// sort
		if (sortField != null) {
			Collections.sort(data, new LazySorter(sortField, sortOrder));
		}

		// TOTAL rowCount
		int dataSize = data.size();
		this.setRowCount(data.size());

		// paginate
		
		if (dataSize > pageSize) {
			try {
				data = data.subList(first, first + pageSize);
			} catch (IndexOutOfBoundsException e) {
				data = data.subList(first, first + (dataSize % pageSize));
			}
		}
		

		
		List<Mail> returnData = new ArrayList<Mail>();
		for (Mail mail : data) {
			returnData.add(mail);
			addChildren(returnData);
			
		}

//		// data is limited to filtered row count
//		this.setRowChildrenCount(returnData.size() - data.size());

		return returnData;
	}
		
	
	private void addChildren(List<Mail> list) {

		//List<Mail> descendents = new ArrayList<>
		for (Mail mail : list.get(list.size()-1).children) {
			list.add(mail);
			addChildren(list);
		}

	}
	
}
