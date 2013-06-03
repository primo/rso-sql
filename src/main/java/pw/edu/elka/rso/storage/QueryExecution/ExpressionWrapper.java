package pw.edu.elka.rso.storage.QueryExecution;

import pw.edu.elka.rso.storage.DataRepresentation.Record;

/**
 */
public class ExpressionWrapper {
    protected String col1;
    protected String col2;
    protected Operation op;

    public ExpressionWrapper(Operation op, String col2, String col1) {
        this.op = op;
        this.col2 = col2;
        this.col1 = col1;
    }

    public boolean evaluate(Record r1, Record r2) {
        switch (op) {
            case EQ:
                if (r1.getValue(col1) == r2.getValue(col2)) {
                    return true;
                }
                return false;
            case GT:
                if (((Comparable<?>)r1.getValue(col1)).compareTo (r2.getValue(col2))>0) {
                    return true;
                }
                return false;
            case LT:
                if (((Comparable<?>)r1.getValue(col1)).compareTo(r2.getValue(col2)) < 0) {
                    return true;
                }
                return false;
            case GTE:
                if (((Comparable<?>)r1.getValue(col1)).compareTo(r2.getValue(col2)) >= 0) {
                    return true;
                }
                return false;
            case LTE:
                if (((Comparable<?>)r1.getValue(col1)).compareTo(r2.getValue(col2)) <= 0) {
                    return true;
                }
                return false;
            default:
                throw new UnsupportedOperationException("Unknown expression operator: " + op.toString());
        }
    }
}

enum Operation {
    EQ, GT, LT , GTE, LTE
}