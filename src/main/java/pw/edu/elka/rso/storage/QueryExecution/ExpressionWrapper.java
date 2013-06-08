package pw.edu.elka.rso.storage.QueryExecution;

import pw.edu.elka.rso.storage.DataRepresentation.Record;

/**
 */
class ExpressionWrapper {
    public final String tab1;
    public final String tab2;
    public final String col1;
    public final String col2;
    public final Operation op;

    public ExpressionWrapper(String tab1, String col1, Operation op,  String tab2, String col2) {
        this.op = op;
        this.col2 = col2;
        this.col1 = col1;
        this.tab1 = tab1;
        this.tab2 = tab2;
    }

    public boolean evaluate(Record r1, Record r2) {
        Object temp = r1.getValue(col1);
        switch (op) {
            case EQ:
                if (temp instanceof String) {
                    if (((String)temp).compareTo((String)r2.getValue(col2)) == 0) {
                        return true;
                    }
                    return false;
                }
                else if (temp instanceof Integer) {
                    if (((Comparable<Integer>)temp).compareTo ((Integer)r2.getValue(col2)) == 0)
                        return true;
                    return false;
                } else if(temp instanceof Double)                                                          {
                    if (((Comparable<Double>)temp).compareTo ((Double)r2.getValue(col2)) == 0)
                        return true;
                    return false;
                }
                return false;
            case GT:
                if (temp instanceof String) {
                    if (((String)temp).compareTo((String)r2.getValue(col2)) > 0) {
                        return true;
                    }
                    return false;
                }
                else if (temp instanceof Integer) {
                    if (((Comparable<Integer>)temp).compareTo ((Integer)r2.getValue(col2)) > 0)
                        return true;
                    return false;
                } else if(temp instanceof Double)                                                          {
                    if (((Comparable<Double>)temp).compareTo ((Double)r2.getValue(col2)) > 0)
                        return true;
                    return false;
                }
                return false;
            case LT:
                if (temp instanceof String) {
                    if (((String)temp).compareTo((String)r2.getValue(col2)) < 0) {
                        return true;
                    }
                    return false;
                }
                else if (temp instanceof Integer) {
                    if (((Comparable<Integer>)temp).compareTo ((Integer)r2.getValue(col2)) < 0)
                        return true;
                    return false;
                } else if(temp instanceof Double)                                                          {
                    if (((Comparable<Double>)temp).compareTo ((Double)r2.getValue(col2)) < 0)
                        return true;
                    return false;
                }
                return false;
            case GTE:
                if (temp instanceof String) {
                    if (((String)temp).compareTo((String)r2.getValue(col2)) >= 0) {
                        return true;
                    }
                    return false;
                }
                else if (temp instanceof Integer) {
                    if (((Comparable<Integer>)temp).compareTo ((Integer)r2.getValue(col2)) >= 0)
                        return true;
                    return false;
                } else if(temp instanceof Double)                                                          {
                    if (((Comparable<Double>)temp).compareTo ((Double)r2.getValue(col2)) >= 0)
                        return true;
                    return false;
                }
                return false;
            case LTE:
                if (temp instanceof String) {
                    if (((String)temp).compareTo((String)r2.getValue(col2)) <= 0) {
                        return true;
                    }
                    return false;
                }
                else if (temp instanceof Integer) {
                    if (((Comparable<Integer>)temp).compareTo ((Integer)r2.getValue(col2)) <= 0)
                        return true;
                    return false;
                } else if(temp instanceof Double)                                                          {
                    if (((Comparable<Double>)temp).compareTo ((Double)r2.getValue(col2)) <= 0)
                        return true;
                    return false;
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