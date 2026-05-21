
export interface Project {
  id?: string;
  projectName: string;
}

export interface Skill {
  id?: string;
  skillName: string;
}


export interface Person {
  id: string;
  name: string;
  age: number;
  email: string;
  password: string;
  role: string;

  projects?: Project[];
  skills?: Skill[];
}

export type CreatePersonDto = Omit<Person, 'id'>;
export type UpdatePersonDto = Omit<Person, 'id'>;

