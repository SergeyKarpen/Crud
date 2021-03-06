package com.sergeykarpen.crudconsole.repository.io;

import com.sergeykarpen.crudconsole.model.AccountStatus;
import com.sergeykarpen.crudconsole.model.Developer;
import com.sergeykarpen.crudconsole.model.Skill;
import com.sergeykarpen.crudconsole.repository.AccountRepository;
import com.sergeykarpen.crudconsole.repository.DeveloperRepository;
import com.sergeykarpen.crudconsole.repository.SkillRepository;

import java.io.IOException;
import java.util.*;

import static com.sergeykarpen.crudconsole.model.AccountStatus.DELETED;
import static com.sergeykarpen.crudconsole.model.AccountStatus.valueOf;
import static com.sergeykarpen.crudconsole.util.IOUtil.*;

public class JavaIODeveloperRepositoryImpl implements DeveloperRepository {

    private SkillRepository skillRepository = new JavaIOSkillRepositoryImpl() {
    };
    private final static String relativePathToFile = "src\\main\\resources\\developers.txt";

    @Override
    public List<Developer> getAll() throws IOException {
        return convertStringsToObjects(readFile((getPathToFile(relativePathToFile))));
    }

    @Override
    public Developer getById(Long id) {
        Developer developer = new Developer();
        try {
            for (Developer needDeveloper : convertStringsToObjects(readFile((getPathToFile(relativePathToFile))))) {
                if (needDeveloper.getId().equals(id)) {
                    developer = needDeveloper;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return developer;
    }

    public Developer save(Developer developer) throws IOException {
        writeInFileString(convertObjectToString(developer), relativePathToFile);
        return developer;
    }


    @Override
    public void deleteById(Long id) throws IOException {
        List<Developer> developers = null;
        try {
            developers = new ArrayList<>(convertStringsToObjects(readFile(getPathToFile(relativePathToFile))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert developers != null;
        for (Developer d : developers) {
            if (d.getId().equals(id)) {
                String s = String.valueOf((AccountStatus.DELETED));
                d.setAccountStatus(s);
            }
        }
        writeInFileList(convertObjectsToStrings(developers), relativePathToFile);
    }

    @Override
    public List<Developer> convertStringsToObjects(List<String> input) throws IOException {
        List<Developer> developers = new ArrayList<>();
        for (String str : input) {
            String[] splitedString = str.split(",");
            Developer developer = new Developer();
            developer.setId((long) Integer.parseInt(splitedString[0]));
            developer.setName(splitedString[1]);
            developer.setAccountId((long) Integer.parseInt(splitedString[2]));
            developer.setAccountStatus(String.valueOf(((splitedString[3]))));
            String[] sIds = splitedString[4].split("/");
            Set<Long> skillIds = new HashSet<>();
            Set<Skill> skills = new HashSet<>();
            for (String id : sIds) {
                try {
                    skillIds.add(Long.valueOf(id));
                    skills.add(skillRepository.getById(Long.valueOf(id)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            developer.setSkillIds(skillIds);
            developer.setSkills(skills);
            developers.add(developer);
        }
        return developers;
    }

    @Override
    public List<String> convertObjectsToStrings(List<Developer> developers) throws IOException {
        List<String> listStrings = new ArrayList<>();
        for (Developer d : developers) {
            listStrings.add(convertObjectToString(d));
        }
        return listStrings;
    }

    @Override
    public String convertObjectToString(Developer developer) throws IOException {
        String convertString = developer.getId() + "," + developer.getName() + "," + developer.getAccountId() + "," + developer.getAccountStatus() + ",";
        StringJoiner stringJoiner = new StringJoiner("/");
        for (Long aLong : developer.getSkillIds()
        ) {
            stringJoiner.add(aLong + "");
        }
        convertString += stringJoiner;
        return convertString+"\n";
    }

    @Override
    public Developer update(Developer developer) throws IOException {
        List<Developer> developers = null;
        try {
            developers = new ArrayList<>(convertStringsToObjects(readFile(getPathToFile(relativePathToFile))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Developer d : developers) {
            if (d.getId().equals(developer.getId())) {
                d.setName(developer.getName());
            }
        }
        writeInFileList(convertObjectsToStrings(developers), relativePathToFile);
        return developer;
    }
}