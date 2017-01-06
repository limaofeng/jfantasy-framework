package org.jfantasy.filestore.service;

import org.jfantasy.filestore.bean.FilePart;
import org.jfantasy.filestore.dao.FilePartDao;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
@Transactional
public class FilePartService {

    private final FilePartDao filePartDao;

    @Autowired
    public FilePartService(FilePartDao filePartDao) {
        this.filePartDao = filePartDao;
    }

    public void save(String path,String namespace, String entireFileHash, String partFileHash, Integer total, Integer index) {
        if (this.findByPartFileHash(entireFileHash, partFileHash) != null) {
            return;
        }
        FilePart part = new FilePart();
        part.setPath(path);
        part.setNamespace(namespace);
        part.setEntireFileHash(entireFileHash);
        part.setPartFileHash(partFileHash);
        part.setTotal(total);
        part.setIndex(index);
        this.filePartDao.save(part);
    }

    public void delete(String path) {
        this.filePartDao.delete(path);
    }

    public List<FilePart> find(String entireFileHash) {
        return this.filePartDao.find(new Criterion[]{Restrictions.eq("entireFileHash", entireFileHash)}, "index", "asc");
    }

    public FilePart findByPartFileHash(String entireFileHash, String partFileHash) {
        return this.filePartDao.findUnique(Restrictions.eq("entireFileHash", entireFileHash), Restrictions.eq("partFileHash", partFileHash));
    }
}
