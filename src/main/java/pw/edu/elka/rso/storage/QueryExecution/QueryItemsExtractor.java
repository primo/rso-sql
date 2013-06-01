package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Extracts items from a list in form (1,2,3,4,...) or a subselect.
 */
class QueryItemsExtractor implements ItemsListVisitor, ExpressionVisitor {

    List<Object> items = new LinkedList<Object>();

    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException("Parsing subselects is not yet implemeneted.");
    }

    @Override
    public void visit(ExpressionList expressionList) {
        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext(); ) {
            Expression e = (Expression) iter.next();
            e.accept(this);
        }
    }

    void appendObject(Object obj) {
        items.add(obj);
    }

    @Override
    public void visit(NullValue nullValue) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Function function) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(InverseExpression inverseExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        // Using autoboxing
        appendObject(doubleValue.getValue());
    }

    @Override
    public void visit(LongValue longValue) {
        appendObject(longValue.getValue());
    }

    @Override
    public void visit(DateValue dateValue) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(TimeValue timeValue) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(StringValue stringValue) {
        appendObject(stringValue.getValue());
    }

    @Override
    public void visit(Addition addition) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Division division) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Multiplication multiplication) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Subtraction subtraction) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(AndExpression andExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(OrExpression orExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Between between) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(InExpression inExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(MinorThan minorThan) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Column column) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(WhenClause whenClause) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Concat concat) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(Matches matches) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throw new UnsupportedOperationException("Expression extraction for this data type is not implemented.");
    }
}

















