package domain.client;

import enums.DocumentType;

import java.util.Objects;

public class Document {

    private DocumentType documentType;
    private String content;

    public Document(DocumentType documentType, String content) {
        this.documentType = documentType;
        this.content = content;
    }

    public DocumentType getDocumentType() {
        return this.documentType;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return documentType == document.documentType && Objects.equals(content, document.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentType, content);
    }

    @Override
    public String toString() {
        return "Document{" + "documentType=" + documentType +
                ", content='" + content + '\'' +
                '}';
    }
}
