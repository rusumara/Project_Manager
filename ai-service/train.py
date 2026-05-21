import random
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.model_selection import train_test_split
import joblib

random.seed(42)
np.random.seed(42)

SKILL_GROUPS = {
    "Backend":      ["Java", "Spring", "Python", "C#", ".NET", "Node.js", "REST", "SQL", "Hibernate"],
    "Frontend":     ["React", "Angular", "Vue", "HTML", "CSS", "JavaScript", "TypeScript", "Sass"],
    "Data Science": ["Python", "ML", "TensorFlow", "PyTorch", "SQL", "R", "Pandas", "NumPy"],
    "DevOps":       ["Docker", "Kubernetes", "AWS", "CI/CD", "Linux", "Git", "Terraform", "Jenkins"],
    "Mobile":       ["Android", "iOS", "React Native", "Flutter", "Swift", "Kotlin"],
    "FullStack":    ["JavaScript", "TypeScript", "React", "Node.js", "SQL", "Git"],
    "QA":           ["Testing", "Selenium", "JUnit", "Postman", "Cypress", "Jest"],
}


def generate_sample(project_type: str):
    core = SKILL_GROUPS[project_type]
    n = random.randint(2, min(5, len(core)))
    selected = random.sample(core, n)
    if random.random() > 0.7:
        others = [s for t, ss in SKILL_GROUPS.items() for s in ss if t != project_type]
        selected += random.sample(others, min(2, len(others)))
    return list(set(selected)), project_type


rows = []
for _ in range(1000):
    ptype = random.choice(list(SKILL_GROUPS.keys()))
    skills, label = generate_sample(ptype)
    rows.append({"skills": ",".join(skills), "project_type": label})

df = pd.DataFrame(rows)
df.to_csv("training_data.csv", index=False)
print(f"Generated {len(df)} training samples")

mlb = MultiLabelBinarizer()
X = mlb.fit_transform(df["skills"].str.split(","))
y = df["project_type"]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

clf = RandomForestClassifier(n_estimators=100, random_state=42)
clf.fit(X_train, y_train)

accuracy = clf.score(X_test, y_test)
print(f"Test accuracy: {accuracy:.2f}")

joblib.dump(clf, "model.pkl")
joblib.dump(mlb, "binarizer.pkl")
print("Saved model.pkl and binarizer.pkl")
