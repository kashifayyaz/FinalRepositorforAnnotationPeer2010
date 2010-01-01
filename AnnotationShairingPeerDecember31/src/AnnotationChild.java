/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import javax.persistence.Id;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Indexed

public class AnnotationChild implements Serializable{
    @Id
    private int childId;
    @Field
    private String queryTerm;
    @Field
    private String text;
    @Field
    private String concept;

    //@ManyToOne
    //private Annotation annID;

    private String localAnnId;

    protected AnnotationChild() {
    }

    public AnnotationChild(String annId, int id,String queryTerm, String text, String concept) {
        this.localAnnId = annId;
        this.childId = id;
        this.queryTerm = queryTerm;
        this.text = text;
        this.concept = concept;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getLocalAnnId() {
        return localAnnId;
    }

    public void setLocalAnnId(String localAnnId) {
        this.localAnnId = localAnnId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getQueryTerm() {
        return queryTerm;
    }

    public void setQueryTerm(String queryTerm) {
        this.queryTerm = queryTerm;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
