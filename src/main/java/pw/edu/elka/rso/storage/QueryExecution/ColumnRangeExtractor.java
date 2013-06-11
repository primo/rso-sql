package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.*;

enum Operation {
    EQ, NEQ, LT, LTE, GT, GTE
}

/**
 */
public class ColumnRangeExtractor implements ExpressionVisitor {

    private List<SelectRange> ranges;
    private String lastTable;
    private String lastColumn;
    private Operation lastOperation;
    private Object lastArgument;

    private boolean fullScan;

    SelectRange rangeAnd(SelectRange s1, SelectRange s2) {
        Object resultantFrom = null;
        Object resultantTo = null;
        boolean resFromIncl = false;
        boolean resToIncl = false;
        if (null == s1.from) {
            resultantFrom = s2.from;
            resFromIncl = s2.fromInclusive;
        }
        if (null == s2.from) {
            resultantFrom = s1.from;
            resFromIncl = s1.fromInclusive;
        }
        if (null == s1.to) {
            resultantTo = s2.to;
            resFromIncl = s2.toInclusive;
        }
        if (null == s2.to) {
            resultantTo = s1.to;
            resFromIncl = s1.toInclusive;
        }
        Comparable from = (Comparable) resultantFrom;
        Comparable to = (Comparable) resultantTo;
        if (from.compareTo(to) < 0) {
            return null;
        }
        SelectRange temp = new SelectRange();
        temp.from = resultantFrom;
        temp.to = resultantTo;
        temp.table = s1.table;
        temp.column = s1.column;
        temp.fromInclusive = resFromIncl;
        temp.toInclusive = resToIncl;
        return temp;
    }

    /**
     * Returns map of SelectRanges
     * TableName => List of 'SelectRange's
     *
     * @return
     */
    public Map<String, List<SelectRange>> getRanges() {
        // TODO make it possible to check different columns
        // TODO OR and serious AND :)
        Map<String, List<SelectRange>> ret = new HashMap<String, List<SelectRange>>();
        // First group by tables
        for (SelectRange sr : ranges) {
            List<SelectRange> selectRanges = ret.get(sr.table);
            if (null == selectRanges) {
                // Fill the Map
                selectRanges = new ArrayList<SelectRange>();
                ret.put(sr.table, selectRanges);
            }
            selectRanges.add(sr);
        }
        for (List<SelectRange> lsr : ret.values()) {
            // For a single column
            final int size = lsr.size();
            if (2 < size) {
                throw new IllegalArgumentException("Illegal WHERE clause - only two elements are allowed.");
            }
            if (2 == size) {
                SelectRange s1 = lsr.get(0);
                SelectRange s2 = lsr.get(1);
                lsr.clear();
                SelectRange temp = rangeAnd(s1, s2);
                if (null != temp) {
                    lsr.add(temp);
                }
            }
        }
        return ret;
    }


    public boolean isFullScan() {
        return fullScan;
    }

    public ColumnRangeExtractor() {
        ranges = new ArrayList<SelectRange>();

    }

    @Override
    public void visit(Column tableColumn) {
        lastColumn = tableColumn.getColumnName();
        lastTable = tableColumn.getTable().getName();
    }

    @Override
    public void visit(MinorThan minorThan) {
        SelectRange sr = new SelectRange();
        minorThan.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        minorThan.getRightExpression().accept(this);
        sr.to = lastArgument;
        sr.op = Operation.LT;
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        SelectRange sr = new SelectRange();
        minorThanEquals.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        minorThanEquals.getRightExpression().accept(this);
        sr.to = lastArgument;
        sr.toInclusive = true;
        sr.op = Operation.LTE;
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        SelectRange sr = new SelectRange();
        notEqualsTo.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        notEqualsTo.getRightExpression().accept(this);
        sr.to = lastArgument;
        sr.toInclusive = true;
        sr.op = Operation.NEQ;
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        SelectRange sr = new SelectRange();
        equalsTo.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        equalsTo.getRightExpression().accept(this);
        sr.to = lastArgument;
        sr.toInclusive = true;
        sr.op = Operation.NEQ;
        ranges.add(sr);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        SelectRange sr = new SelectRange();
        greaterThan.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        greaterThan.getRightExpression().accept(this);
        sr.from = lastArgument;
        sr.fromInclusive = false;
        sr.op = Operation.GT;
        ranges.add(sr);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        SelectRange sr = new SelectRange();
        greaterThanEquals.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        greaterThanEquals.getRightExpression().accept(this);
        sr.from = lastArgument;
        sr.fromInclusive = true;
        sr.op = Operation.GTE;
        ranges.add(sr);
    }

    @Override
    public void visit(AndExpression andExpression) {
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);

    }

    @Override
    public void visit(OrExpression orExpression) {
        orExpression.getLeftExpression().accept(this);
        orExpression.getRightExpression().accept(this);
        fullScan = true;
    }

    @Override
    public void visit(NullValue nullValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Function function) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(InverseExpression inverseExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        lastArgument = doubleValue.getValue();
    }

    @Override
    public void visit(LongValue longValue) {
        lastArgument = longValue.getValue();
    }

    @Override
    public void visit(DateValue dateValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(TimeValue timeValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(StringValue stringValue) {
        lastArgument = stringValue.getValue();
    }

    @Override
    public void visit(Addition addition) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Division division) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Multiplication multiplication) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Subtraction subtraction) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Between between) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(InExpression inExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(SubSelect subSelect) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(WhenClause whenClause) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Concat concat) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Matches matches) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
