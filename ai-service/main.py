from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Any, Dict
import joblib
import numpy as np
import re

app = FastAPI()

clf = joblib.load("model.pkl")
mlb = joblib.load("binarizer.pkl")


class PredictRequest(BaseModel):
    skills: List[str]


class PredictResponse(BaseModel):
    projectType: str
    confidence: float


class ChatRequest(BaseModel):
    message: str
    people: List[Dict[str, Any]] = []


class ChatResponse(BaseModel):
    answer: str


@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    X = mlb.transform([req.skills])
    proba = clf.predict_proba(X)[0]
    idx = int(np.argmax(proba))
    project_type = clf.classes_[idx]
    confidence = round(float(proba[idx]), 2)
    return PredictResponse(projectType=project_type, confidence=confidence)


def fetch_people():
    try:
        resp = requests.get(f"{SPRING_BOOT_URL}/person", timeout=5)
        resp.raise_for_status()
        return resp.json()
    except Exception:
        return None


@app.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest):
    msg = req.message.strip()
    people = req.people  # list of PersonResponseDTO: {name, email, skills: [str], projects: [str]}

    m = re.search(r"who has (.+)", msg, re.IGNORECASE)
    if m:
        skill = m.group(1).strip().rstrip("?")
        matches = [
            f"{p['name']} ({p['email']})"
            for p in people
            if any(s.lower() == skill.lower() for s in p.get("skills", []))
        ]
        if matches:
            return ChatResponse(answer=f"People with {skill}: {', '.join(matches)}")
        return ChatResponse(answer=f"No one found with skill: {skill}")

    m = re.search(r"what skills does (.+?) have", msg, re.IGNORECASE)
    if m:
        name = m.group(1).strip().rstrip("?")
        for p in people:
            if p["name"].lower() == name.lower():
                skills = p.get("skills", [])
                if skills:
                    return ChatResponse(answer=f"{p['name']}'s skills: {', '.join(skills)}")
                return ChatResponse(answer=f"{p['name']} has no skills assigned.")
        return ChatResponse(answer=f"Person not found: {name}")

    m = re.search(r"what projects does (.+?) have", msg, re.IGNORECASE)
    if m:
        name = m.group(1).strip().rstrip("?")
        for p in people:
            if p["name"].lower() == name.lower():
                projects = p.get("projects", [])
                if projects:
                    return ChatResponse(answer=f"{p['name']}'s projects: {', '.join(projects)}")
                return ChatResponse(answer=f"{p['name']} has no projects assigned.")
        return ChatResponse(answer=f"Person not found: {name}")

    m = re.search(r"who works on (.+)", msg, re.IGNORECASE)
    if m:
        project_name = m.group(1).strip().rstrip("?")
        matches = [
            f"{p['name']} ({p['email']})"
            for p in people
            if any(pr.lower() == project_name.lower() for pr in p.get("projects", []))
        ]
        if matches:
            return ChatResponse(answer=f"People on {project_name}: {', '.join(matches)}")
        return ChatResponse(answer=f"No one found working on: {project_name}")

    return ChatResponse(
        answer="I can answer questions like: who has Java? what skills does Ana have? what projects does Ana have? who works on Alpha?"
    )
