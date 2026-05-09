# Practice 8

2026-04-26
Deadline: Check onsite on or before week 12 lab.

In this practice, we'll modify Teedy's tests in order to improve its test coverage.

## Preparation

First, you should follow the tutorial so that:
You could execute the original tests included in Teedy
You could use JaCoCo on Teedy to generate test coverage report.

## Task

The JaCoCo report shows that the current test suite for Teedy has low instruction coverage and branch coverage.
You should add one or more test cases in order to improve both these two coverages.
A crucial part of this task is to understand Teedy's source code and test code. You could pick any element (e.g., class, method) within any module in Teedy as the target to be tested by your test cases.

## Evaluation

To demonstrate that you've completed the task, you should show us:
The original JaCoCo test coverage report.
The test cases that you've added
Execute the new test cases (mvn -Dtest=YourNewTestClass test)
Run jacoco:report again and show us the new JaCoCo test coverage report, which should have increased instruction coverage and branch coverage compared to the original report.
