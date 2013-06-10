package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.*;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

enum Operation {
    EQ, NEQ, LT, LTE, GT, GTE
}

/**
 */
public class ColumnRangeExtractor implements ExpressionVisitor{

    private Deque<SelectRange> ranges;
    private String lastTable;
    private String lastColumn;
    private Operation lastOperation;
    private Object lastArgument;


    /** Returns map of SelectRanges
     * TableName => List of 'SelectRange's
     * @return
     */
    public Map<String, List<SelectRange>> getRanges() {
//            FIXME
        Map<String, List<SelectRange>> ret = new HashMap<String, List<SelectRange>>();
//        // First group by tables
//        for (SelectRange sr : ranges) {
//            List<SelectRange> selectRanges = ret.get(sr.table);
//            if (null == selectRanges) {
//                // Fill the Map
//                selectRanges = new ArrayList<SelectRange>();
//                ret.put(sr.table, selectRanges);
//            }
//            selectRanges.add(sr);
//        }
//        // Merge selectRanges and sort them
//        SortedMap<Integer, SelectRange> index = new TreeMap<Integer, SelectRange>();
//        for (List<SelectRange> lsr : ret.values()) {
//
//            index.clear();
//        }
        return ret;
    }

    public ColumnRangeExtractor() {
        ranges = new ArrayDeque<SelectRange>();

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
        ranges.push(sr);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        SelectRange sr = new SelectRange();
        greaterThan.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        greaterThan.getRightExpression().accept(this);
        sr.from = lastArgument;
        sr.toInclusive = false;
        sr.op = Operation.GT;
        ranges.push(sr);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        SelectRange sr = new SelectRange();
        greaterThanEquals.getLeftExpression().accept(this);
        sr.column = lastColumn;
        sr.table = lastTable;
        greaterThanEquals.getRightExpression().accept(this);
        sr.from = lastArgument;
        sr.toInclusive = true;
        sr.op = Operation.GTE;
        ranges.push(sr);
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
