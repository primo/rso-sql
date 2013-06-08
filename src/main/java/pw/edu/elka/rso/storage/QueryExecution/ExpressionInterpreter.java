package pw.edu.elka.rso.storage.QueryExecution;

import java.util.AbstractMap;
import java.util.Map.Entry;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.LinkedList;
import java.util.List;

/**
 */
public class ExpressionInterpreter implements ExpressionVisitor {

    public final List<ExpressionWrapper> expressions = new LinkedList<ExpressionWrapper>();
    private Entry<String,String> lastColumn;

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(LongValue longValue) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
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
    public void visit(AndExpression andExpression) {
        andExpression.accept(this);
    }

    @Override
    public void visit(OrExpression orExpression) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Between between) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void wrapExpression(BinaryExpression expr, Operation op) {
        expr.getLeftExpression().accept(this);
        Entry<String,String> c1 = lastColumn;
        expr.getRightExpression().accept(this);
        Entry<String,String> c2 = lastColumn;
        ExpressionWrapper ew = new ExpressionWrapper(c1.getKey(),c1.getValue(), op, c2.getKey(),c2.getValue());
        expressions.add(ew);
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        wrapExpression(equalsTo, Operation.EQ);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        wrapExpression(greaterThan, Operation.GT);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        wrapExpression(greaterThanEquals, Operation.GTE);
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
    public void visit(MinorThan minorThan) {
        wrapExpression(minorThan, Operation.LT);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        wrapExpression(minorThanEquals, Operation.LTE);

    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(Column column) {
        lastColumn = new AbstractMap.SimpleEntry<String, String>(column.getTable().getName(), column.getColumnName());
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
