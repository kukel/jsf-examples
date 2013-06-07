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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import net.vankuijk.ronald.jsfexamples.domain.File;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@SuppressWarnings("serial")
@Named("interchangesBean")
@SessionScoped
public class InterchangesBean implements Serializable {

	private final static Logger logger = Logger.getLogger(InterchangesBean.class.getName());

	private List<File> interchanges;

	public List<File> getInterchanges() {
		return interchanges;
	}

	public void setInterchanges(List<File> interchanges) {
		this.interchanges = interchanges;
	}

	private List<File> files;

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	private File selectedFile;

	public InterchangesBean() {
	}

	@PostConstruct
	public void init() {
		interchanges = new ArrayList<File>();
		populateRandomInterchanges(interchanges, 10);
		files = new ArrayList<File>();
		files.addAll(interchanges);
		//files.add(interchanges.get((int) (Math.random() * 10)));
	}

	public File getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}

	private void populateRandomInterchanges(List<File> list, int size) {
		for (int i = 0; i < size; i++) {
			File file = new File(getRandomId());
			list.add(file);
			int levels = (int) (Math.random() * 4);

			populateRandomChildren(file, levels, 0);
		}
	}

	private void populateRandomChildren(File parent, int levels, int level) {

		if (level < levels) {
			int children = (int) (Math.random() * 3) - level;

			for (int i = 0; i < children; i++) {
				File child = new File(getRandomId());
				parent.files.add(child);
				populateRandomChildren(child, levels, level + 1);
			}
		}
	}

	private String getRandomId() {
		return UUID.randomUUID().toString().substring(0, 8);

	}

	private Date getRandomDate() {

		return new Date((long) (Math.random() * new Date().getTime()));
	}

	public void onRowSelect(SelectEvent event) {
		// selectedMail = (Mail) event.getObject();
		// String subject = "????";
		// if (selectedMail != null) {
		// subject = selectedMail.getSubject();
		// }
		// FacesMessage msg = new FacesMessage("Mail Selected", subject);
		//
		// FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onRowUnselect(UnselectEvent event) {
		// FacesMessage msg = new FacesMessage("Mail Unselected", ((Mail) event.getObject()).getSubject());

		// FacesContext.getCurrentInstance().addMessage(null, msg);
	}

}
