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
package net.vankuijk.ronald.jsfexamples.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Mail implements Serializable {

	public String id;
    public String from;
    public String subject;
    public String body;
    public Date date;
    public Set<Mail> children;
    
    public Mail() {}

    public Mail(String id, String from, String subject, String body, Date date) {
        this.id = id;
    	this.from = from;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.children = new HashSet<Mail>();
    }

    public String getId() {
    	return id;
    }
    
    public void setId(String id) {
    	this.id=id;
    }
    
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

	public Set<Mail> getChildren() {
		return children;
	}

	public void setChildren(Set<Mail> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "Mail [id=" + id + ", subject=" + subject + ", from=" + from + ", date=" + date + ", body=" + body
				+ ", children=" + (children == null ? 0 : children.size()) + "]";
	}
}
