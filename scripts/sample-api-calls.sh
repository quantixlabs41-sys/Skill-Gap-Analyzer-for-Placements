#!/bin/bash
# Sample API calls for Skill Gap Analyzer

echo "Analyze student 1 vs company 1"
curl -s "http://localhost:8080/api/gap/analyze?studentId=1&companyId=1" | jq

echo "Analyze student 1 vs all companies"
curl -s "http://localhost:8080/api/gap/analyze/all?studentId=1" | jq
