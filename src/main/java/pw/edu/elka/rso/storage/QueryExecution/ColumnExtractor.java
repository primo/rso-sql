package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

/**
 */
public class ColumnExtractor implements ExpressionVisitor {
    public final List<SimpleEntry<String,String>> columns = new ArrayList<SimpleEntry<String, String>>();

    @Override
    public void visit(NullValue nullValue) {
        return;
    }

    @Override
    public void visit(Function function) {
        return;
    }

    @Override
    public void visit(InverseExpression inverseExpression) {
        return;
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        return;
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        return;
    }

    @Override
    public void visit(LongValue longValue) {
        return;
    }

    @Override
    public void visit(DateValue dateValue) {
        return;
    }

    @Override
    public void visit(TimeValue timeValue) {
        return;
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        return;
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        return;
    }

    @Override
    public void visit(StringValue stringValue) {
        return;
    }

    @Override
    public void visit(Addition addition) {
        return;
    }

    @Override
    public void visit(Division division) {
        return;
    }

    @Override
    public void visit(Multiplication multiplication) {
        return;
    }

    @Override
    public void visit(Subtraction subtraction) {
        return;
    }

    @Override
    public void visit(AndExpression andExpression) {
        andExpression.accept(this);
    }

    @Override
    public void visit(OrExpression orExpression) {
        orExpression.accept(this);
    }

    @Override
    public void visit(Between between) {
        return;
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        equalsTo.accept(this);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        greaterThan.accept(this);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        greaterThanEquals.accept(this);
    }

    @Override
    public void visit(InExpression inExpression) {
        return;
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        isNullExpression.accept(this);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        return;
    }

    @Override
    public void visit(MinorThan minorThan) {
        minorThan.accept(this);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        minorThanEquals.accept(this);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        return;
    }

    @Override
    public void visit(Column tableColumn) {
        String column = tableColumn.getColumnName();
        String table = tableColumn.getTable().getName();
        columns.add(new SimpleEntry<String, String>(column,table));
    }

    @Override
    public void visit(SubSelect subSelect) {
        return;
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        return;
    }

    @Override
    public void visit(WhenClause whenClause) {
        return;
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        return;
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        return;
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        return;
    }

    @Override
    public void visit(Concat concat) {
        return;
    }

    @Override
    public void visit(Matches matches) {
        return;
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        return;
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        return;
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        return;
    }
}
