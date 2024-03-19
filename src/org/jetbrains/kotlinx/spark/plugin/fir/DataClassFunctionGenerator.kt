//package org.jetbrains.kotlinx.spark.plugin.fir
//
//import com.intellij.psi.JavaCodeFragment
//import org.jetbrains.kotlin.GeneratedDeclarationKey
//import org.jetbrains.kotlin.descriptors.ClassKind
//import org.jetbrains.kotlin.descriptors.Modality
//import org.jetbrains.kotlin.fir.FirSession
//import org.jetbrains.kotlin.fir.declarations.utils.isData
//import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
//import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
//import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
//import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
//import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
//import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
//import org.jetbrains.kotlin.fir.extensions.utils.AbstractSimpleClassPredicateMatchingService
//import org.jetbrains.kotlin.fir.plugin.createMemberFunction
//import org.jetbrains.kotlin.fir.plugin.createTopLevelClass
//import org.jetbrains.kotlin.fir.resolve.fqName
//import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
//import org.jetbrains.kotlin.fir.symbols.impl.*
//import org.jetbrains.kotlin.fir.types.ConeClassLikeType
//import org.jetbrains.kotlin.fir.types.ConeKotlinType
//import org.jetbrains.kotlin.name.*
//import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
//
///*
// * Generates top level class
// *
// * public final class foo.bar.MyClass {
// *     fun foo(): String = "Hello world"
// * }
// */
//
///**
// * Generates extra functions inside `@Sparkify` annotated data classes like:
// *
// * ```kt
// * @Sparkify
// * data class User(
// *    val name: String = "John Doe",
// *    val age: Int = 25,
// * ) {
// *   fun name(): String = this.name // GENERATED
// *   fun age(): Int = this.age // GENERATED
// * }
// * ```
// */
//class DataClassFunctionGenerator(session: FirSession) :
//    FirDeclarationGenerationExtension(session) {
//
//    private val predicate by lazy {
//        LookupPredicate.create {
//            val fqNames = session.sparkifyPredicateMatcher.sparkifyAnnotationFqNames
//                .map { FqName(it) }.toMutableList()
//            val first = fqNames.removeFirst()
//
//            fqNames.fold(annotated(first)) { acc, fqName ->
//                acc or annotated(fqName)
//            }
//        }
//    }
//
//    private val predicateBasedProvider = session.predicateBasedProvider
//    private val matchedClasses by lazy {
//        predicateBasedProvider.getSymbolsByPredicate(predicate)
//            .filterIsInstance<FirRegularClassSymbol>()
//            .filter { it.isData }
//    }
//
//    @Suppress("CheckedExceptionsKotlin")
//    private val FirClassSymbol<*>.constructorProperties
//        get() = session.sparkifyPredicateMatcher.propertyCache.getOrPut(this) {
//            buildMap {
//                val constructor = declarationSymbols.filterIsInstance<FirConstructorSymbol>().first { it.isPrimary }
//                for (property in constructor.valueParameterSymbols) {
//                    val name = property.callableId.callableName
//                    val type = property.resolvedReturnType.type
//
//                    this[name] = type
//                }
//            }
//        }
//
//    override fun getTopLevelClassIds(): Set<ClassId> =
//        matchedClasses.map { it.classId.sparkify() }.toSet()
//
//    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
//        if (!classId.isSparkified()) return super.generateTopLevelClassLikeDeclaration(classId)
//        return createTopLevelClass(classId = classId, key = Key, classKind = ClassKind.INTERFACE).symbol
//    }
//
//    private fun shouldProcess(symbol: FirClassSymbol<*>): Boolean =
//        symbol.classId.isSparkified()
//
//    private fun getOriginalClass(symbol: FirClassSymbol<*>): FirClassSymbol<*> {
//        val originalClassId = symbol.classId.unSparkify()
//        return matchedClasses.find { it.classId == originalClassId }!!
//    }
//
//    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
//        if (shouldProcess(classSymbol)) {
//            getOriginalClass(classSymbol)
//                .constructorProperties
//                .keys
//                .toSet()
//        } else {
//            emptySet()
//        }
//
//    override fun generateFunctions(
//        callableId: CallableId,
//        context: MemberGenerationContext?
//    ): List<FirNamedFunctionSymbol> {
//        val owner = context?.owner ?: return emptyList()
//        if (!shouldProcess(owner)) return emptyList()
//
//        val type = getOriginalClass(owner).constructorProperties[callableId.callableName] ?: return emptyList()
//
//        val function = createMemberFunction(
//            owner = owner,
//            key = Key,
//            name = callableId.callableName,
//            returnType = type,
//        )
//
//        return listOf(function.symbol)
//    }
//
//
//    object Key : GeneratedDeclarationKey()
//}
//
//class FirSparkifyPredicateMatcher(
//    session: FirSession,
//    val sparkifyAnnotationFqNames: List<String>
//) : AbstractSimpleClassPredicateMatchingService(session) {
//    companion object {
//        fun getFactory(sparkifyAnnotationFqNames: List<String>): Factory {
//            return Factory { session -> FirSparkifyPredicateMatcher(session, sparkifyAnnotationFqNames) }
//        }
//    }
//
//    val propertyCache: MutableMap<FirClassSymbol<*>, Map<Name, ConeKotlinType>> = mutableMapOf()
//
//    override val predicate = DeclarationPredicate.create {
//        val annotationFqNames = sparkifyAnnotationFqNames.map { FqName(it) }
//        annotated(annotationFqNames) or metaAnnotated(annotationFqNames, includeItself = true)
//    }
//}
//
//val FirSession.sparkifyPredicateMatcher: FirSparkifyPredicateMatcher by FirSession.sessionComponentAccessor()