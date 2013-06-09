package pw.edu.elka.rso.storage.QueryExecution;

import java.util.*;
import java.util.Map.Entry;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;
import pw.edu.elka.rso.storage.DataRepresentation.Record;
import pw.edu.elka.rso.storage.DataRepresentation.Table;


/**
 */
public class ExpressionInterpreter implements ExpressionVisitor {

    public static final Object NullObject = new Object();

    private HashMap<String, Record> currentLines;
    private HashMap<String, Table>  subjectTables ;
    private HashMap<String, String>  aliases ;
    private HashMap<String, String> columnMapping ;
    private Deque<Object> elements = new ArrayDeque<Object>();

    public ExpressionInterpreter(HashMap<String, Record> currentLines, HashMap<String, Table> subjectTables, HashMap<String, String> aliases, HashMap<String, String> columnMapping) {
        this.currentLines = currentLines;
        this.subjectTables = subjectTables;
        this.aliases = aliases;
        this.columnMapping = columnMapping;
    }



    @Override
    public void visit(NullValue nullValue) {
        elements.add(NullObject);
    }

    @Override
    public void visit(Function function) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(InverseExpression inverseExpression) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        elements.add(doubleValue.getValue());
    }

    @Override
    public void visit(LongValue longValue) {
        elements.add(longValue.getValue());
    }

    @Override
    public void visit(DateValue dateValue) {
        throw new IllegalArgumentException("Date data type is not yet supported.");
    }

    @Override
    public void visit(TimeValue timeValue) {
        throw new IllegalArgumentException("Time data type is not yet supported.");
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        throw new IllegalArgumentException("Timestamp data type is not yet supported.");
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        Expression e = parenthesis.getExpression();
        e.accept(this);
    }

    @Override
    public void visit(StringValue stringValue) {
        elements.add(stringValue.getValue());
    }

    @Override
    public void visit(Addition addition) {
        Expression l = addition.getLeftExpression();
        Expression r = addition.getRightExpression();
        l.accept(this);
        r.accept(this);
        Object ro = elements.pop();
        Object lo = elements.pop();
        // TODO elements.add(r+l)
    }

    @Override
    public void visit(Division division) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(Multiplication multiplication) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(Subtraction subtraction) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(AndExpression andExpression) {
        Expression l = andExpression.getLeftExpression();
        Expression r  = andExpression.getRightExpression();
        l.accept(this);
        r.accept(this);
        elements.add( (Boolean)elements.pop() && (Boolean)elements.pop());
    }

    @Override
    public void visit(OrExpression orExpression) {
        Expression l = orExpression.getLeftExpression();
        Expression r  = orExpression.getRightExpression();
        l.accept(this);
        r.accept(this);
        elements.add( (Boolean)elements.pop() || (Boolean)elements.pop());
    }

    @Override
    public void visit(Between between) {
        throw new IllegalArgumentException("Between");
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        Expression l = equalsTo.getLeftExpression();
        Expression r = equalsTo.getRightExpression();
        l.accept(this);
        r.accept(this);
        // Equality is symmetric
        Comparable rc = (Comparable) elements.pop();
        Comparable lc = (Comparable) elements.pop();

        try {
            if (0 == lc.compareTo(rc)) {
                elements.add(Boolean.valueOf(true));
            } else {
                elements.add(Boolean.valueOf(false));
            }
        } catch (ClassCastException ce) {
            elements.add(Boolean.valueOf(false));
        }
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        Expression l = greaterThan.getLeftExpression();
        Expression r = greaterThan.getRightExpression();
        l.accept(this);
        r.accept(this);
        // Equality is symmetric
        Comparable rc = (Comparable) elements.pop();
        Comparable lc = (Comparable) elements.pop();

        try {
            if (0 <= lc.compareTo(rc)) {
                elements.add(Boolean.valueOf(true));
            } else {
                elements.add(Boolean.valueOf(false));
            }
        } catch (ClassCastException ce) {
            elements.add(Boolean.valueOf(false));
        }
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        Expression l = greaterThanEquals.getLeftExpression();
        Expression r = greaterThanEquals.getRightExpression();
        l.accept(this);
        r.accept(this);
        // Equality is symmetric
        Comparable rc = (Comparable) elements.pop();
        Comparable lc = (Comparable) elements.pop();

        try {
            if (0 < lc.compareTo(rc)) {
                elements.add(Boolean.valueOf(true));
            } else {
                elements.add(Boolean.valueOf(false));
            }
        } catch (ClassCastException ce) {
            elements.add(Boolean.valueOf(false));
        }
    }

    @Override
    public void visit(InExpression inExpression) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        Expression l = isNullExpression.getLeftExpression();
        l.accept(this);
        elements.add( (elements.pop() == NullObject && !isNullExpression.isNot()) ||
                (elements.pop() != NullObject && isNullExpression.isNot()));
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(MinorThan minorThan) {
        Expression l = minorThan.getLeftExpression();
        Expression r = minorThan.getRightExpression();
        l.accept(this);
        r.accept(this);
        // Equality is symmetric
        Comparable rc = (Comparable) elements.pop();
        Comparable lc = (Comparable) elements.pop();

        try {
            if (0 > lc.compareTo(rc)) {
                elements.add(Boolean.valueOf(true));
            } else {
                elements.add(Boolean.valueOf(false));
            }
        } catch (ClassCastException ce) {
            elements.add(Boolean.valueOf(false));
        }
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        Expression l = minorThanEquals.getLeftExpression();
        Expression r = minorThanEquals.getRightExpression();
        l.accept(this);
        r.accept(this);
        // Equality is symmetric
        Comparable rc = (Comparable) elements.pop();
        Comparable lc = (Comparable) elements.pop();

        try {
            if (0 >= lc.compareTo(rc)) {
                elements.add(Boolean.valueOf(true));
            } else {
                elements.add(Boolean.valueOf(false));
            }
        } catch (ClassCastException ce) {
            elements.add(Boolean.valueOf(false));
        }

    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        Expression l = notEqualsTo.getLeftExpression();
        Expression r = notEqualsTo.getRightExpression();
        l.accept(this);
        r.accept(this);
        // Equality is symmetric
        Comparable rc = (Comparable) elements.pop();
        Comparable lc = (Comparable) elements.pop();

        try {
            if (0 != lc.compareTo(rc)) {
                elements.add(Boolean.valueOf(true));
            } else {
                elements.add(Boolean.valueOf(false));
            }
        } catch (ClassCastException ce) {
            elements.add(Boolean.valueOf(false));
        }
    }

    @Override
    public void visit(Column column) {
        // Get column value
        final String colname = column.getColumnName();
        final String tabname = column.getTable().getName();
        final String tabalias = column.getTable().getName();
        Object out = null;
        if (null != tabname) {
            Record r = currentLines.get(tabname);
            out = r.getValue(colname);
        } else if(null != tabalias) {
            String name = aliases.get(tabalias);
            Record r = currentLines.get(tabname);
            out = r.getValue(colname);
        } else {
            String temp = columnMapping.get(colname);
            Record r = currentLines.get(tabname);
            out = r.getValue(colname);
        }
        elements.add(out);
    }

    @Override
    public void visit(SubSelect subSelect) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(WhenClause whenClause) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(Concat concat) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(Matches matches) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throw new IllegalArgumentException("Operation not yet implemented.");
    }

    public boolean isTrue() {
        return (Boolean)elements.pop();
    }
}
