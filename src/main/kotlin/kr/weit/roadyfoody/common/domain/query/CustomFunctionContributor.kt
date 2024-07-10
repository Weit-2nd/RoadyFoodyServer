package kr.weit.roadyfoody.common.domain.query

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

class CustomFunctionContributor : FunctionContributor {
    override fun contributeFunctions(functionContributions: FunctionContributions) {
        functionContributions.functionRegistry.register(
            "myContains",
            StandardSQLFunction("contains", StandardBasicTypes.INTEGER),
        )
    }
}
