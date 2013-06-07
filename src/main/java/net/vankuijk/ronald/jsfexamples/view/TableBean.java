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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import java.util.*;
import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import net.vankuijk.ronald.jsfexamples.domain.Mail;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

@SuppressWarnings("serial")
@Named("tableBean")
@SessionScoped
public class TableBean implements Serializable {

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("model", "manufacturer", "year", "color");

	private final static Logger logger = Logger.getLogger(TableBean.class.getName());

	private final static String[] from;

	private final static String[] text;

	private String theme;

	private String columnTemplate = "model manufacturer year";

	static {
		from = new String[4];
		from[0] = "someone@adomain.com";
		from[1] = "otherone@adomain.com";
		from[2] = "anyone@otherdomain.net";
		from[3] = "noone@nodomain.nl";

		text = new String[10];
		text[0] = "Mercedes";
		text[1] = "BMW";
		text[2] = "Volvo";
		text[3] = "Audi";
		text[4] = "Renault";
		text[5] = "Opel";
		text[6] = "Volkswagen";
		text[7] = "Chrysler";
		text[8] = "Ferrari";
		text[9] = "Ford";
	}

	private List<Mail> filteredMails;

	private List<Mail> mails;

	private Date date = new Date();

	private Mail selectedMail;

	private Mail[] selectedMails;

	private LazyDataModel<Mail> lazyModel;

	private String columnName;

	private List<ColumnModel> columns = new ArrayList<ColumnModel>();

	private boolean editMode;

	private TreeNode availableColumns;

	public TableBean() {}
	
	@PostConstruct
	public void init() {
	
		mails = new ArrayList<Mail>();

		populateRandomMails(mails, 50);

		createDynamicColumns();

		if (lazyModel == null) {
			lazyModel = new LazyMailDataModel(mails);
		}

		System.out.println(lazyModel + " " + this);
		createAvailableColumns();
	}

	public LazyDataModel<Mail> getLazyModel() {
		return lazyModel;
	}
	
	public boolean hasChilderen(Mail mail) {
		
		return mail==null ? false : mail.getChildren().size() > 0;
	}

	public Mail[] getSelectedMails() {
		return selectedMails;
	}

	public void setSelectedMails(Mail[] selectedMails) {
		this.selectedMails = selectedMails;
	}

	public Mail getSelectedMail() {
		return selectedMail;
	}

	public void setSelectedMail(Mail selectedMail) {
		this.selectedMail = selectedMail;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	private void populateRandomMails(List<Mail> list, int size) {
		for (int i = 0; i < size; i++) {
			Mail mail = new Mail(getRandomId(), getRandomFrom(), getRandomSubject(), getRandomBody(), getRandomDate());
			list.add(mail);
			int levels = (int) (Math.random() * 4);

			populateRandomChildren(mail, levels, 0);
		}
	}

	private void populateRandomChildren(Mail parent, int levels, int level) {

		if (level < levels) {
			int children = (int) (Math.random() * 3) - level;

			for (int i = 0; i < children; i++) {
				Mail child = new Mail(getRandomId(), getRandomFrom(), getRandomSubject(), getRandomBody(), getRandomDate());
				parent.getChildren().add(child);
				populateRandomChildren(child, levels, level + 1);
			}
		}
	}

	// private void populateLazyRandomMails(List<Mail> list, int size) {
	// for (int i = 0; i < size; i++) {
	// list.add(new Mail(getRandomId(), getRandomFrom(), getRandomSubject(), getRandomBody(), getRandomDate()));
	//
	// }
	// }

	public List<Mail> getFilteredMails() {
		return filteredMails;
	}

	public void setFilteredMails(List<Mail> filteredMails) {
		this.filteredMails = filteredMails;
	}

	public List<Mail> getMails() {
		return mails;
	}

	private String getRandomSubject() {
		return "" + (int) (Math.random() * 50 + 1960);
	}

	private String getRandomId() {
		return UUID.randomUUID().toString().substring(0, 8);

	}

	private Date getRandomDate() {

		return new Date((long) (Math.random() * new Date().getTime()));
	}

	private String getRandomBody() {
		return text[(int) (Math.random() * 10)];
	}

	public int getRandomPrice() {
		return (int) (Math.random() * 100000);
	}

	private String getRandomFrom() {
		return from[(int) (Math.random() * 4)];

	}

	public void save() {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Changes Saved"));
	}

	public void onRowSelect(SelectEvent event) {
//		selectedMail = (Mail) event.getObject();
		String subject = "????";
		if (selectedMail != null) {
			subject = selectedMail.getSubject();
		}
		FacesMessage msg = new FacesMessage("Mail Selected", subject);

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onRowUnselect(UnselectEvent event) {
		FacesMessage msg = new FacesMessage("Mail Unselected", ((Mail) event.getObject()).getSubject());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public String onRowSelectNavigate(SelectEvent event) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("selectedMail", event.getObject());

		return "mailDetail?faces-redirect=true";
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String[] getManufacturers() {
		return text;
	}

	public String[] getColors() {
		return from;
	}

	private SelectItem[] createFilterOptions(String[] data) {
		SelectItem[] options = new SelectItem[data.length + 1];

		options[0] = new SelectItem("", "Select");
		for (int i = 0; i < data.length; i++) {
			options[i + 1] = new SelectItem(data[i], data[i]);
		}

		return options;
	}

	private void createAvailableColumns() {
		availableColumns = new DefaultTreeNode("Root", null);
		TreeNode root = new DefaultTreeNode("Columns", availableColumns);
		root.setExpanded(true);
		TreeNode model = new DefaultTreeNode("column", new ColumnModel("Model", "model"), root);
		TreeNode year = new DefaultTreeNode("column", new ColumnModel("Year", "year"), root);
		TreeNode manufacturer = new DefaultTreeNode("column", new ColumnModel("Manufacturer", "manufacturer"), root);
		TreeNode color = new DefaultTreeNode("column", new ColumnModel("Color", "color"), root);
	}

	static public class ColumnModel implements Serializable {

		private String header;
		private String property;

		public ColumnModel(String header, String property) {
			this.header = header;
			this.property = property;
		}

		public String getHeader() {
			return header;
		}

		public String getProperty() {
			return property;
		}
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public String navigate() {
		return "home";
	}

	public void onEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Mail Edited", ((Mail) event.getObject()).getSubject());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Mail Cancelled", ((Mail) event.getObject()).getSubject());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onResize(ColumnResizeEvent event) {
		FacesMessage msg = new FacesMessage("Column " + event.getColumn().getClientId() + " resized", "W:" + event.getWidth()
				+ ", H:" + event.getHeight());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public TreeNode getAvailableColumns() {
		return availableColumns;
	}

	public String getColumnTemplate() {
		return columnTemplate;
	}

	public void setColumnTemplate(String columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void updateColumns() {
		// reset table state
		UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:mails");
		table.setValueExpression("sortBy", null);

		// update columns
		createDynamicColumns();
	}

	public void createDynamicColumns() {
		String[] columnKeys = columnTemplate.split(" ");
		columns.clear();

		for (String columnKey : columnKeys) {
			String key = columnKey.trim();

			if (VALID_COLUMN_KEYS.contains(key)) {
				columns.add(new ColumnModel(columnKey.toUpperCase(), columnKey));
			}
		}
	}

	public void onRowToggle(ToggleEvent event) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Row State " + event.getVisibility(), "Model:"
				+ ((Mail) event.getData()).getSubject());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void treeToTable() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String property = params.get("property");
		String droppedColumnId = params.get("droppedColumnId");
		String dropPos = params.get("dropPos");

		String[] droppedColumnTokens = droppedColumnId.split(":");
		int draggedColumnIndex = Integer.parseInt(droppedColumnTokens[droppedColumnTokens.length - 1]);
		int dropColumnIndex = draggedColumnIndex + Integer.parseInt(dropPos);

		// add to columns
		this.columns.add(dropColumnIndex, new ColumnModel(property.toUpperCase(), property));

		// remove from nodes
		TreeNode root = availableColumns.getChildren().get(0);
		for (TreeNode node : root.getChildren()) {
			ColumnModel model = (ColumnModel) node.getData();
			if (model.getProperty().equals(property)) {
				root.getChildren().remove(node);
				break;
			}
		}
	}

	public void tableToTree() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		int colIndex = Integer.parseInt(params.get("colIndex"));

		// remove from table
		ColumnModel model = this.columns.remove(colIndex);

		// add to nodes
		TreeNode property = new DefaultTreeNode("column", model, availableColumns.getChildren().get(0));
	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();

		if (newValue != null && !newValue.equals(oldValue)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:"
					+ newValue);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
}
