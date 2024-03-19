//package org.jetbrains.kotlinx.spark.plugin.fir
//
//import org.jetbrains.kotlin.fir.FirSession
//import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
//import org.jetbrains.kotlin.fir.declarations.utils.classId
//import org.jetbrains.kotlin.fir.declarations.utils.isData
//import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
//import org.jetbrains.kotlin.fir.resolve.fqName
//import org.jetbrains.kotlin.fir.symbols.impl.ConeClassLikeLookupTagImpl
//import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
//import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
//import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
//import org.jetbrains.kotlin.name.ClassId
//import org.jetbrains.kotlin.name.FqName
//
//class DataClassSuperTypesGenerator(session: FirSession) : FirSupertypeGenerationExtension(session) {
//
//    context(TypeResolveServiceContainer)
//    override fun computeAdditionalSupertypes(
//        classLikeDeclaration: FirClassLikeDeclaration,
//        resolvedSupertypes: List<FirResolvedTypeRef>,
//    ): List<FirResolvedTypeRef> = listOf(
//        buildResolvedTypeRef {
//            val newClassId = classLikeDeclaration.classId.sparkify()
//
//            type = ConeClassLikeTypeImpl(
//                lookupTag = ConeClassLikeLookupTagImpl(newClassId),
//                typeArguments = emptyArray(),
//                isNullable = false,
//            )
//        }
//    )
//
//    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean =
//        declaration.symbol.isData &&
//                declaration.annotations.any {
//                    it.fqName(session)?.asString() in session.sparkifyPredicateMatcher.sparkifyAnnotationFqNames
//                }
//}
//
//fun ClassId.sparkify(): ClassId =
//    copy(relativeClassName = FqName(relativeClassName.asString() + "_Sparkify"))
//
//fun ClassId.unSparkify(): ClassId {
//    require(isSparkified()) { "Class $this is not sparkified" }
//    return copy(relativeClassName = FqName(relativeClassName.asString().removeSuffix("_Sparkify")))
//}
//
//fun ClassId.isSparkified(): Boolean = relativeClassName.asString().endsWith("_Sparkify")