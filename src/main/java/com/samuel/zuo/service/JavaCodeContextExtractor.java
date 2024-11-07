package com.samuel.zuo.service;


import com.intellij.psi.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * description: JavaCodeContextExtractor
 * date: 2024/8/9 16:38
 * author: samuel_zuo
 * version: 1.0
 */
public class JavaCodeContextExtractor {

    private final Map<PsiMethod, Set<PsiMethod>> callGraph = new HashMap<>();

    public void buildCallGraph(PsiFile psiFile) {
        psiFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                Set<PsiMethod> calledMethods = new HashSet<>();
                method.accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                        super.visitMethodCallExpression(expression);
                        PsiMethod calledMethod = expression.resolveMethod();
                        if (calledMethod != null) {
                            calledMethods.add(calledMethod);
                        }
                    }
                });
                callGraph.put(method, calledMethods);
            }
        });
    }

    public Map<PsiMethod, Set<PsiMethod>> getCallGraph() {
        return callGraph;
    }

    public static String printMethodSignature(PsiMethod method) {
        // add method return type and access modifier
        StringBuilder methodSignature = new StringBuilder(getMethodModifiers(method) + " " +method.getReturnType().getPresentableText() + " " + method.getName() + " (");
        for (int i = 0; i < method.getParameterList().getParametersCount(); i++) {
            methodSignature.append(method.getParameterList().getParameters()[i].getType().getPresentableText()).append(" ").append(method.getParameterList().getParameters()[i].getName());
            if (i < method.getParameterList().getParametersCount() - 1) {
                methodSignature.append(", ");
            }
        }
        methodSignature.append(")");
        return methodSignature.toString();
    }

    public static String getMethodModifiers(PsiMethod method) {
        PsiModifierList modifierList = method.getModifierList();
        StringBuilder modifiers = new StringBuilder();

        if (modifierList.hasModifierProperty(PsiModifier.PUBLIC)) {
            modifiers.append(PsiModifier.PUBLIC).append(' ');
        } else if (modifierList.hasModifierProperty(PsiModifier.PRIVATE)) {
            modifiers.append(PsiModifier.PRIVATE).append(' ');
        } else if (modifierList.hasModifierProperty(PsiModifier.PROTECTED)) {
            modifiers.append(PsiModifier.PROTECTED).append(' ');
        } else if (modifierList.hasModifierProperty(PsiModifier.PACKAGE_LOCAL)) {
            // package-private (no explicit modifier)
            modifiers.append(' ');
        }

        if (modifierList.hasModifierProperty(PsiModifier.STATIC)) {
            modifiers.append(PsiModifier.STATIC).append(' ');
        }
        if (modifierList.hasModifierProperty(PsiModifier.FINAL)) {
            modifiers.append(PsiModifier.FINAL).append(' ');
        }
        if (modifierList.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
            modifiers.append(PsiModifier.SYNCHRONIZED).append(' ');
        }
        if (modifierList.hasModifierProperty(PsiModifier.ABSTRACT)) {
            modifiers.append(PsiModifier.ABSTRACT).append(' ');
        }
        if (modifierList.hasModifierProperty(PsiModifier.TRANSIENT)) {
            modifiers.append(PsiModifier.TRANSIENT).append(' ');
        }
        if (modifierList.hasModifierProperty(PsiModifier.VOLATILE)) {
            modifiers.append(PsiModifier.VOLATILE).append(' ');
        }
        if (modifierList.hasModifierProperty(PsiModifier.NATIVE)) {
            modifiers.append(PsiModifier.NATIVE).append(' ');
        }
        if (modifierList.hasModifierProperty(PsiModifier.STRICTFP)) {
            modifiers.append(PsiModifier.STRICTFP).append(' ');
        }

        // Trim trailing space and return
        return modifiers.toString().trim();
    }
}
